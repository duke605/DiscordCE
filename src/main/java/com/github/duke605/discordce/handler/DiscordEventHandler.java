package com.github.duke605.discordce.handler;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.contract.CustomListenerAdapter;
import com.github.duke605.discordce.entity.Relationship;
import com.github.duke605.discordce.event.RelationshipAddEvent;
import com.github.duke605.discordce.event.RelationshipRemoveEvent;
import com.github.duke605.discordce.gui.GuiServers;
import com.github.duke605.discordce.lib.Config;
import com.github.duke605.discordce.lib.Preferences;
import com.github.duke605.discordce.lib.VolatileSettings;
import com.github.duke605.discordce.util.ConcurrentUtil;
import com.github.duke605.discordce.util.DiscordUtil;
import com.github.duke605.discordce.util.MCHelper;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.client.JDAClient;
import net.dv8tion.jda.client.entities.impl.JDAClientImpl;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.requests.Requester;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class DiscordEventHandler extends CustomListenerAdapter {

    @Override
    public void onReady(ReadyEvent e)
    {
        // Updating player status
        ConcurrentUtil.executor.execute(() -> e.getJDA().getAccountManager().setGame("Minecraft [Menus]"));
        DiscordCE.client = (JDAClient) e.getJDA();

        // Setting interceptor
        ((JDAClientImpl) DiscordCE.client)
                .getClient()
                .setCustomHandler(new CustomWebSocketHandler((JDAClientImpl) DiscordCE.client));

        // Setting default guild and channel
        if (Preferences.i.usingChannel.equals("") && e.getJDA().getGuilds().size() > 0)
        {
            Preferences.i.usingGuild = e.getJDA().getGuilds().get(0).getId();
            Preferences.i.usingChannel = e.getJDA().getGuilds().get(0).getPublicChannel().getId();
            Preferences.save();
        }

        // Getting block list
        ConcurrentUtil.executor.execute(() -> {
            Requester.Response r = ((JDAClientImpl) DiscordCE.client).getRequester()
                    .get(Requester.DISCORD_API_PREFIX + "users/@me/relationships");

            if (!r.isOk())
                return;

            for(Object o :  r.getArray()) {
                JSONObject jo = (JSONObject) o;

                VolatileSettings.relationships.put(jo.getString("id"), new Relationship(jo));
            }
        });

        // Adding hook to shutdown discordce when user exists
        Runtime.getRuntime().addShutdownHook(new Thread(() -> DiscordCE.client.shutdown(true)));
    }

    @Override
    public void onGuildAvailable(GuildAvailableEvent e)
    {
        // Populating
        if (Preferences.i.usingGuild.equals("")) {
            Preferences.i.usingGuild = e.getGuild().getId();
            Preferences.i.usingChannel = e.getGuild().getPublicChannel().getId();
            Preferences.save();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e)
    {
        if (Minecraft.getMinecraft().thePlayer == null
                || VolatileSettings.isBlocked(e.getAuthor().getId())
                || Preferences.i.mutedChannels.contains(e.getChannel().getId())
                || Preferences.i.mutedGuilds.contains(e.getGuild().getId())
                || (Preferences.i.focus
                    && !Preferences.i.usingChannel.equals(e.getChannel().getId())))
            return;

        String message;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        // Formatting message
        message = MCHelper.buildInteractiveMessage(
                e.getAuthorName()
                , e.getAuthorNick()
                , e.getAuthor().getId()
                , e.getGuild()
                , e.getChannel()
                , DiscordUtil.resolveMentions(e.getMessage().getRawContent())
                , Config.serverLayout
                , e.getMessage());

        Minecraft
                .getMinecraft()
                .thePlayer
                .addChatComponentMessage(TextComponentBase.Serializer.jsonToComponent(message));
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e)
    {
        if (Minecraft.getMinecraft().thePlayer == null
                || VolatileSettings.isBlocked(e.getAuthor().getId()))
            return;

        String message;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        // Highlighting mentions
        message = MCHelper.buildInteractiveMessage(
                e.getAuthor().getUsername()
                , null
                , e.getAuthor().getId()
                , null
                , null
                , DiscordUtil.resolveMentions(e.getMessage().getRawContent())
                , Config.directLayout
                , e.getMessage());

        Minecraft
                .getMinecraft()
                .thePlayer
                .addChatComponentMessage(TextComponentBase.Serializer.jsonToComponent(message));
    }

    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent e)
    {
        String prev = e.getPrevNick() == null
                ? e.getUser().getUsername()
                : e.getPrevNick();
        String now = e.getNewNick() == null
                ? e.getUser().getUsername()
                : e.getNewNick();

        JSONArray message = new JSONArray()
                .put("")
                .put(MCHelper.mentionAsRaw(e.getUser().getId(), prev))
                .put(" changed their name to ")
                .put(MCHelper.mentionAsRaw(e.getUser().getId(), now))
                .put(" on ")
                .put(MCHelper.serverAsRaw(e.getUser().getId(), e.getGuild().getName()))
                .put(".");

        MCHelper.sendRawMessage(message);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e)
    {
        if (Minecraft.getMinecraft().thePlayer == null
                || !e.getUser().getId().equals(DiscordCE.client.getSelfInfo().getId())
                || Minecraft.getMinecraft().currentScreen == null
                || !(Minecraft.getMinecraft().currentScreen instanceof GuiServers))
            return;

        Minecraft.getMinecraft().currentScreen.initGui();
    }

    @Override
    public void onUserNameUpdate(UserNameUpdateEvent e)
    {
        JSONArray message = new JSONArray()
                .put("")
                .put(MCHelper.mentionAsRaw(e.getUser().getId(), e.getPreviousUsername()))
                .put(" changed their name to ")
                .put(MCHelper.mentionAsRaw(e.getUser().getId(), e.getUser().getUsername()))
                .put(".");

        MCHelper.sendRawMessage(message);
    }

    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent e)
    {
        if (e.getPreviousOnlineStatus() != OnlineStatus.OFFLINE
                || DiscordCE.client == null
                || Minecraft.getMinecraft().thePlayer == null
                || !VolatileSettings.isFriend(e.getUser().getId()))
            return;

        JSONArray message = new JSONArray()
                .put("")
                .put(MCHelper.mentionAsRaw(e.getUser().getId(), e.getUser().getUsername()))
                .put(" is online.");

        MCHelper.sendRawMessage(message);
    }

    @Override
    public void onRelationshipAdd(RelationshipAddEvent e) {
        VolatileSettings.relationships.put(e.relationship.id, e.relationship);

        switch(e.relationship.type) {
            case Relationship.BLOCK:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("")
                        .put(MCHelper.mentionAsRaw(e.relationship.user.getId(), e.relationship.user.getUsername()))
                        .put(" has ben blocked."));
                return;
            case Relationship.FRIEND:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("")
                        .put(MCHelper.mentionAsRaw(e.relationship.user.getId(), e.relationship.user.getUsername()))
                        .put(" has been added as a friend."));
                return;
            case Relationship.INCOMING:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("")
                        .put(MCHelper.mentionAsRaw(e.relationship.user.getId(), e.relationship.user.getUsername())
                                .put("clickEvent", new JSONObject()
                                    .put("action", "run_command")
                                    .put("value", "/addfriend " + e.relationship.id))
                                .put("hoverEvent", new JSONObject()
                                    .put("action", "show_text")
                                    .put("value", "Click to accept friend request.\n" +
                                                  "Shift + Click to append channel's id.")))
                        .put(" wants to add you as a friend."));
                return;
            case Relationship.OUTGOING:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("A friend request has been sent to ")
                        .put(MCHelper.mentionAsRaw(e.relationship.user.getId(), e.relationship.user.getUsername())));
        }
    }

    @Override
    public void onRelationshipRemove(RelationshipRemoveEvent e) {
        Relationship r = VolatileSettings.relationships.get(e.id);

        if (r == null)
            return;

        switch(e.type) {
            case Relationship.BLOCK:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("")
                        .put(MCHelper.mentionAsRaw(r.user.getId(), r.user.getUsername()))
                        .put(" has ben unblocked."));
                break;
            case Relationship.FRIEND:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("")
                        .put(MCHelper.mentionAsRaw(r.user.getId(), r.user.getUsername()))
                        .put(" is no longer a friend."));
                break;
            case Relationship.OUTGOING:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("Your friend request to ")
                        .put(MCHelper.mentionAsRaw(r.user.getId(), r.user.getUsername()))
                        .put(" has been canceled."));
                break;
            case Relationship.INCOMING:
                MCHelper.sendRawMessage(new JSONArray()
                        .put("Friend request from ")
                        .put(MCHelper.mentionAsRaw(r.user.getId(), r.user.getUsername()))
                        .put(" has been canceled."));
                break;
        }

        VolatileSettings.relationships.remove(e.id);
    }
}
