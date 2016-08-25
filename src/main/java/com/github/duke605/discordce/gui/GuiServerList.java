package com.github.duke605.discordce.gui;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.discordce.gui.abstraction.GuiEntry;
import com.github.duke605.discordce.gui.abstraction.GuiListButton;
import com.github.duke605.discordce.lib.Preferences;
import com.github.duke605.discordce.util.Arrays;
import net.dv8tion.jda.entities.Guild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class GuiServerList extends GuiEmbeddedList
{
    private GuiServers guiServers;
    private List<GuiEntry> entries;

    public GuiServerList(Minecraft mc, GuiServers guiServers)
    {
        super(mc
                , guiServers.width + 100
                , guiServers.height
                , 16 + mc.fontRendererObj.FONT_HEIGHT
                , guiServers.height - 50
                , mc.fontRendererObj.FONT_HEIGHT + 16);

        this.guiServers = guiServers;
        entries = new ArrayList<>(DiscordCE.client.getGuilds().size());
        DiscordCE.client.getGuilds().stream()
                .sorted((g, g1) -> g.getName().compareTo(g1.getName()))
                .forEach(g -> entries.add(new GuildEntry(mc.fontRendererObj, g)));
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

    private class GuildEntry extends GuiEntry
    {
        private Guild guild;
        private FontRenderer fr;

        public GuildEntry(FontRenderer fr, Guild guild)
        {
            this.fr = fr;
            this.guild = guild;
            GuiListButton b;

            // Mute server
            guiButtons.add(b = new GuiListButton(0, getListWidth() - 45, 0, 45, 20
                    , Preferences.i.mutedGuilds.contains(guild.getId())
                        ? TextFormatting.RED + "Unmute"
                        : "Mute"));

            guiButtons.add(new GuiListButton(1, getListWidth() - 90, 0, 45, 20, "Leave"));
            guiButtons.add(new GuiListButton(2, getListWidth() - 141, 0, 50, 20, "Channels"));

            // Disabling mute if this is user's current server
            if (Preferences.i.usingGuild.equals(guild.getId()))
                b.enabled = false;
        }

        @Override
        public void drawEntry(int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean isSelected)
        {
            for(GuiListButton button : guiButtons)
                button.drawButton(mc, mouseX, mouseY, x, y);

            // Server name
            this.fr.drawString(Arrays.truncate(guild.getName(), 20)
                    , x - 100
                    , y + (height - this.fr.FONT_HEIGHT + 1) / 2
                    , 0xFFFFFF);

            // Members
            this.fr.drawString("" + guild.getUsers().size()
                    , x + width - 150 - mc.fontRendererObj.getStringWidth("" + guild.getUsers().size())
                    , y + (height - this.fr.FONT_HEIGHT + 1) / 2
                    , 0xFFFFFF);
        }

        protected void actionPerformed(GuiButton button)
        {
            GuiListButton b = (GuiListButton) button;
            // Muting server
            if (b.id == 0)
            {
                if (!Preferences.i.mutedGuilds.contains(guild.getId()))
                    Preferences.i.mutedGuilds.add(guild.getId());
                else
                    Preferences.i.mutedGuilds.remove(guild.getId());

                b.displayString = Preferences.i.mutedGuilds.contains(guild.getId())
                        ? TextFormatting.RED + "Unmute"
                        : "Mute";
            }

            // Leaving server
            else if (b.id == 1)
            {
                GuiYesNo yn = new GuiYesNo((result, id) ->
                {
                    // leaving guild
                    if (result)
                        guild.getManager().leave();

                    mc.displayGuiScreen(guiServers);
                }
                , "Are you sure you want to leave " + guild.getName() + "?"
                , "You'll have to get an invite link to join again."
                , 0);
                mc.displayGuiScreen(yn);
            }

            // Showing channels for guild
            if (b.id == 2)
            {
                mc.displayGuiScreen(new GuiChannels(guiServers, guild));
            }
        }
    }
}
