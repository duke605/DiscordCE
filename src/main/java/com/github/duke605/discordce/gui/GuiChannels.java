package com.github.duke605.discordce.gui;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.contract.CustomListenerAdapter;
import com.github.duke605.discordce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.discordce.gui.abstraction.GuiListContainer;
import com.github.duke605.discordce.lib.Preferences;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.channel.text.*;
import net.dv8tion.jda.events.guild.GuildUpdateEvent;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiChannels extends GuiListContainer
{
    private GuiServers guiServers;
    protected Guild guild;
    private GuiChannelList channelList;
    private CustomListenerAdapter listener;

    public GuiChannels(GuiServers guiServers, Guild guild)
    {
        this.guiServers = guiServers;
        this.guild = guild;
        listener = new CustomListenerAdapter()
        {
            @Override
            public void onTextChannelDelete(TextChannelDeleteEvent e)
            {
                if (e.getGuild().getId().equals(guild.getId()))
                    GuiChannels.this.initGui();
            }

            @Override
            public void onTextChannelUpdateName(TextChannelUpdateNameEvent e)
            {
                if (e.getGuild().getId().equals(guild.getId()))
                    GuiChannels.this.initGui();
            }

            @Override
            public void onTextChannelUpdatePosition(TextChannelUpdatePositionEvent e)
            {
                if (e.getGuild().getId().equals(guild.getId()))
                    GuiChannels.this.initGui();
            }

            @Override
            public void onTextChannelCreate(TextChannelCreateEvent e)
            {
                if (e.getGuild().getId().equals(guild.getId()))
                    GuiChannels.this.initGui();
            }

            @Override
            public void onGuildUpdate(GuildUpdateEvent e)
            {
                if (e.getGuild().getId().equals(guild.getId()))
                    GuiChannels.this.initGui();
            }

            @Override
            public void onTextChannelUpdatePermissions(TextChannelUpdatePermissionsEvent e)
            {
                if (e.getGuild().getId().equals(guild.getId()))
                    GuiChannels.this.initGui();
            }

            @Override
            public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent e)
            {
                if (e.getGuild().getId().equals(guild.getId()))
                    GuiChannels.this.initGui();
            }
        };
    }

    @Override
    public void initGui()
    {
        onGuiClosed();
        DiscordCE.client.addEventListener(listener);
        channelList = new GuiChannelList(mc, this);
        buttonList.clear();
        buttonList.add(new GuiButton(0
                , 10
                , ((fontRendererObj.FONT_HEIGHT + 16)/2) - 9
                , 40
                , 20
                , "< Back"));

        buttonList.add(new GuiButton(1
                , width / 2 + 2
                , height - ((fontRendererObj.FONT_HEIGHT + 16)/2) - 11
                , 80
                , 20
                , "Umute All"));

        buttonList.add(new GuiButton(2
                , width / 2 - 82
                , height - ((fontRendererObj.FONT_HEIGHT + 16)/2) - 11
                , 80
                , 20
                , "Mute All"));
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        channelList.drawScreen(x, y, f);
        drawCenteredString(mc.fontRendererObj, "Channels for " + guild.getName(), width / 2, 8, 0xFFFFFFFF);

        super.drawScreen(x, y, f);
    }

    @Override
    protected void actionPerformed(GuiButton b) throws IOException
    {
        // Going back to servers
        if (b.id == 0)
        {
            mc.displayGuiScreen(guiServers);
        }

        // Muting all channels
        else if (b.id == 1)
        {
            User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());
            guild.getTextChannels().stream()
                    .filter(c -> c.checkPermission(me, Permission.MESSAGE_READ)
                                 && c.checkPermission(me, Permission.MESSAGE_WRITE))
                    .forEach(c ->
                    Preferences.i.mutedChannels.remove(c.getId()));

            Preferences.save();
            initGui();
        }

        // Unmuting all channels
        else if (b.id == 2)
        {
            User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());
            guild.getTextChannels().stream()
                    .filter(c -> c.checkPermission(me, Permission.MESSAGE_READ)
                                 && c.checkPermission(me, Permission.MESSAGE_WRITE))
                    .forEach(c -> {
                        if (!Preferences.i.mutedChannels.contains(c.getId()) && !Preferences.i.usingChannel.equals(c.getId()))
                            Preferences.i.mutedChannels.add(c.getId());
                    });

            Preferences.save();
            initGui();
        }
    }

    @Override
    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) throws IOException
    {
        if(p_keyTyped_2_ == 1)
            this.mc.displayGuiScreen(guiServers);
    }

    @Override
    public void onGuiClosed()
    {
        DiscordCE.client.removeEventListener(listener);
    }

    @Override
    public GuiEmbeddedList getEmbeddedList()
    {
        return channelList;
    }
}
