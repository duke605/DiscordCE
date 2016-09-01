package com.github.duke605.dce.gui;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.dce.gui.abstraction.GuiEntry;
import com.github.duke605.dce.gui.abstraction.GuiListButton;
import com.github.duke605.dce.lib.Preferences;
import com.github.duke605.dce.util.Arrays;
import com.github.duke605.dce.util.DiscordUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.User;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;
import java.util.List;

public class GuiChannelList extends GuiEmbeddedList
{
    private GuiChannels guiChannels;
    private List<IGuiListEntry> entries;

    public GuiChannelList(Minecraft mc, GuiChannels guiChannels)
    {
        super(mc
                , guiChannels.width + 100
                , guiChannels.height
                , 16 + mc.fontRenderer.FONT_HEIGHT
                , guiChannels.height - (16 + mc.fontRenderer.FONT_HEIGHT)
                , mc.fontRenderer.FONT_HEIGHT + 16);
        this.guiChannels = guiChannels;
        entries = new ArrayList<>(guiChannels.guild.getTextChannels().size());
        User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());
        guiChannels.guild.getTextChannels().stream()
                .filter(c -> c.checkPermission(me, Permission.MESSAGE_READ)
                             && c.checkPermission(me, Permission.MESSAGE_WRITE))
                .forEach(g -> entries.add(new ChannelEntry(mc.fontRenderer, g)));
    }

    @Override
    public IGuiListEntry getListEntry(int i)
    {
        return entries.get(i);
    }

    @Override
    protected int getSize()
    {
        return entries.size();
    }

    public class ChannelEntry extends GuiEntry
    {
        private Channel channel;
        private FontRenderer fr;

        public ChannelEntry(FontRenderer fr, Channel channel)
        {
            this.channel = channel;
            this.fr = fr;
            GuiListButton b;

            // Mute channel
            guiButtons.add(b = new GuiListButton(0, getListWidth() - 45, 0, 45, 20
                    , Preferences.i.mutedChannels.contains(channel.getId())
                        ? ChatFormatting.RED + "Unmute"
                        : "Mute"));

            // Disabling switch if its the current channel
            if (Preferences.i.usingChannel.equals(channel.getId()))
                b.enabled = false;

            // Switch channel
            guiButtons.add(b = new GuiListButton(1, getListWidth() - 90, 0, 45, 20, "Switch"));

            // Disabling mute if current channel
            if (Preferences.i.usingChannel.equals(channel.getId()))
                b.enabled = false;
        }

        @Override
        public void drawEntry(int index, int x, int y, int width, int height, Tessellator var6, int mouseX, int mouseY, boolean isSelected)
        {
            for(GuiListButton button : guiButtons)
                button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, x, y);

            // Channel name
            this.fr.drawString(Arrays.truncate(channel.getName(), 30)
                    , x - 100
                    , y
                    , 0xFFFFFF);

            // Channel topic
            String topic = DiscordUtil.resolveMentions(channel.getTopic());
            this.fr.setUnicodeFlag(true);
            this.fr.drawString(
                    Arrays.truncate(topic, 60)
                    , x - 100
                    , y + height - this.fr.FONT_HEIGHT - 1
                    , 0xFFFFFF);
            this.fr.setUnicodeFlag(false);
        }

        protected void actionPerformed(GuiButton button)
        {
            GuiListButton b = (GuiListButton) button;

            // Muting channel
            if (b.id == 0)
            {
                if (!Preferences.i.mutedChannels.contains(channel.getId()))
                    Preferences.i.mutedChannels.add(channel.getId());
                else
                    Preferences.i.mutedChannels.remove(channel.getId());

                b.displayString = Preferences.i.mutedChannels.contains(channel.getId())
                        ? ChatFormatting.RED + "Unmute"
                        : "Mute";
            }

            else if (b.id == 1)
            {
                DiscordUtil.switchToChannel(new String[]{channel.getId()});
                guiChannels.initGui();
            }
        }
    }
}
