package com.github.duke605.dce.gui;

import com.github.duke605.dce.contract.CustomListenerAdapter;
import com.github.duke605.dce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.dce.gui.abstraction.GuiListButton;
import com.github.duke605.dce.gui.abstraction.GuiListContainer;
import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.Preferences;
import com.github.duke605.dce.lib.VolatileSettings;
import com.github.duke605.dce.util.ConcurrentUtil;
import com.github.duke605.dce.util.DrawingUtils;
import com.github.duke605.dce.util.HttpUtil;
import com.github.duke605.dce.util.MCHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.requests.Requester;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.duke605.dce.DiscordCE;

public class GuiServers extends GuiListContainer
{
    private GuiServerList serverList;
    private Minecraft mc;
    private CustomListenerAdapter listener;
    private GuiScreen parent;

    public GuiServers(GuiScreen parent)
    {
        this.parent = parent;
        mc = Minecraft.getMinecraft();
        listener = new CustomListenerAdapter() {

            @Override
            public void onGuildJoin(GuildJoinEvent event)
            {
                GuiServers.this.initGui();
            }

            @Override
            public void onGuildLeave(GuildLeaveEvent event)
            {
                GuiServers.this.initGui();
            }
        };

        // Downloading guild icons
        if (Config.guildIcons)
            DiscordCE.client.getGuilds().forEach(g -> {
                String url = g.getIconUrl();

                if (VolatileSettings.icons.containsKey(url))
                    return;

                VolatileSettings.icons.put(url, null);

                Future<BufferedImage> f = ConcurrentUtil.executor.submit(() ->
                        HttpUtil.getImage(url, DrawingUtils::circularize));

                ConcurrentUtil.pushImageTaskToQueue(f, url);
            });
    }

    @Override
    public void onGuiClosed()
    {
        DiscordCE.client.removeEventListener(listener);
    }

    @Override
    public void initGui()
    {
        DiscordCE.client.removeEventListener(listener);
        DiscordCE.client.addEventListener(listener);
        serverList = new GuiServerList(mc, this);
        buttonList.clear();
        buttonList.add(new GuiButton(-1
                , 10
                , ((fontRendererObj.FONT_HEIGHT + 16)/2) - 9
                , 40
                , 20
                , "< Back"));

        buttonList.add(new GuiListButton(0, (width / 2) - 100, height - ((fontRendererObj.FONT_HEIGHT + 16)/2) - 11, "Join"));
        buttonList.add(new GuiListButton(2
                , (width / 2) - 100
                , height - ((fontRendererObj.FONT_HEIGHT + 16)/2) - 34
                , 99
                , 20
                , "Mute All"));

        buttonList.add(new GuiListButton(1
                , (width / 2) + 1
                , height - ((fontRendererObj.FONT_HEIGHT + 16)/2) - 34
                , 99
                , 20
                , "Unmute All"));
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        serverList.drawScreen(x, y, f);
        drawCenteredString(mc.fontRenderer, "Guilds", width / 2, 8, 0xFFFFFFFF);

        super.drawScreen(x, y, f);
    }

    @Override
    protected void actionPerformed(GuiButton b)
    {
        // Going back
        if (b.id == -1)
            mc.displayGuiScreen(parent);

        // Getting join link
        else if (b.id == 0)
        {
            mc.displayGuiScreen(new GuiInput(link -> {
                try
                {
                    String invite = link;
                    Matcher m = Pattern.compile("(?:https?:\\/\\/)?discord(?:\\.gg|app\\.com\\/invite)\\/(.+?)(?:$|\\/)")
                            .matcher(link);

                    // Checking if a group was found
                    if (m.find())
                        invite = m.group(1);

                    HttpResponse<String> r = Unirest.post(Requester.DISCORD_API_PREFIX + "invite/{code}")
                            .routeParam("code", invite)
                            .header("authorization", DiscordCE.client.getAuthToken())
                            .header("user-agent", Requester.USER_AGENT)
                            .asString();

                    if (r.getStatus() <= 199 || r.getStatus() >= 300) {
                        if (r.getStatus() == 404)
                            MCHelper.sendMessage(ChatFormatting.RED + "That invite is expired or invalid.");
                        else
                            MCHelper.sendMessage(ChatFormatting.RED + r.getStatusText());
                        return;
                    }

                    mc.displayGuiScreen(this);
                } catch (UnirestException e)
                {
                    MCHelper.sendMessage(ChatFormatting.RED + e.getMessage());
                }
            }, "", "Enter an Instant Invite and join your friend's guild."));
        }

        // Muting all guilds
        else if (b.id == 1)
        {
            DiscordCE.client.getGuilds().forEach(g ->
                    Preferences.i.mutedGuilds.remove(g.getId()));

            Preferences.save();
            initGui();
        }

        // Unmuting all guilds
        else if (b.id == 2)
        {
            DiscordCE.client.getGuilds().forEach(g -> {
                        if (!Preferences.i.mutedGuilds.contains(g.getId())
                                && !Preferences.i.usingGuild.equals(g.getId()))
                            Preferences.i.mutedGuilds.add(g.getId());
                    });

            Preferences.save();
            initGui();
        }
    }

    @Override
    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_)
    {
        if(p_keyTyped_2_ == 1)
            this.mc.displayGuiScreen(parent);
    }

    @Override
    public GuiEmbeddedList getEmbeddedList()
    {
        return serverList;
    }
}
