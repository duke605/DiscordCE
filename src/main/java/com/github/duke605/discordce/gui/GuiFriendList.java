package com.github.duke605.discordce.gui;

import com.github.duke605.discordce.entity.Relationship;
import com.github.duke605.discordce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.discordce.gui.abstraction.GuiEntry;
import com.github.duke605.discordce.gui.abstraction.GuiListButton;
import com.github.duke605.discordce.lib.VolatileSettings;
import com.github.duke605.discordce.util.Arrays;
import com.github.duke605.discordce.util.DiscordUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GuiFriendList extends GuiEmbeddedList
{
    private GuiFriends guiFriends;
    private List<IGuiListEntry> entries = new ArrayList<>();

    public GuiFriendList(Minecraft mc, GuiFriends guiFriends)
    {
        super(mc
                , guiFriends.width + 100
                , guiFriends.height
                , 16 + mc.fontRendererObj.FONT_HEIGHT
                , guiFriends.height - (16 + mc.fontRendererObj.FONT_HEIGHT)
                , mc.fontRendererObj.FONT_HEIGHT + 16);
        this.guiFriends = guiFriends;
        initList();
    }

    public void initList()
    {
        // Clearing entries if there was some
        if (entries != null)
            this.entries.clear();

        if (VolatileSettings.relationships.entrySet().stream().anyMatch(r -> r.getValue().type == Relationship.FRIEND))
        {
            this.entries.add(new CategoryEntry(mc.fontRendererObj, "Friends"));
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> r.getValue().type == Relationship.FRIEND)
                    .map(r -> new RelationshipEntry(mc.fontRendererObj, r.getValue()))
                    .collect(Collectors.toList()));
        }

        if (VolatileSettings.relationships.entrySet().stream().anyMatch(r -> r.getValue().type == Relationship.OUTGOING))
        {
            this.entries.add(new CategoryEntry(mc.fontRendererObj, "Pending (Outgoing)"));
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> r.getValue().type == Relationship.OUTGOING)
                    .map(r -> new RelationshipEntry(mc.fontRendererObj, r.getValue()))
                    .collect(Collectors.toList()));
        }

        if (VolatileSettings.relationships.entrySet().stream().anyMatch(r -> r.getValue().type == Relationship.INCOMING))
        {
            this.entries.add(new CategoryEntry(mc.fontRendererObj, "Pending (Incoming)"));
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> r.getValue().type == Relationship.INCOMING)
                    .map(r -> new RelationshipEntry(mc.fontRendererObj, r.getValue()))
                    .collect(Collectors.toList()));
        }

        if (VolatileSettings.relationships.entrySet().stream().anyMatch(r -> r.getValue().type == Relationship.BLOCK))
        {
            this.entries.add(new CategoryEntry(mc.fontRendererObj, "Blocked"));
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> r.getValue().type == Relationship.BLOCK)
                    .map(r -> new RelationshipEntry(mc.fontRendererObj, r.getValue()))
                    .collect(Collectors.toList()));
        }
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

    public class RelationshipEntry extends GuiEntry
    {
        private Relationship relationship;
        private FontRenderer fr;

        public RelationshipEntry(FontRenderer fr, Relationship relationship)
        {
            this.fr = fr;
            this.relationship = relationship;

            // Remove friend
            if (relationship.type == Relationship.FRIEND)
                guiButtons.add(new GuiListButton(0, getListWidth() - 50, 0, 50, 20, "Unfriend"));

            // Ignore request
            else if (relationship.type == Relationship.INCOMING)
            {
                guiButtons.add(new GuiListButton(0, getListWidth() - 50, 0, 50, 20, "Ignore"));
                guiButtons.add(new GuiListButton(1, getListWidth() - 105, 0, 50, 20, "Accept"));
            }

            // Cancel request
            else if (relationship.type == Relationship.OUTGOING)
                guiButtons.add(new GuiListButton(0, getListWidth() - 50, 0, 50, 20, "Cancel"));

            // Unblock
            else if (relationship.type == Relationship.BLOCK)
                guiButtons.add(new GuiListButton(2, getListWidth() - 50, 0, 50, 20, "Unblock"));
        }

        @Override
        public void drawEntry(int index,int x,int y,int width,int height,int mouseX,int mouseY,boolean isSelected)
        {
            for (GuiListButton button : guiButtons)
                button.drawButton(mc, mouseX, mouseY, x, y);

            // Status
            String name;
            switch (relationship.user.getOnlineStatus())
            {
                case ONLINE:
                    name = TextFormatting.GREEN + relationship.user.getUsername();
                    break;

                case OFFLINE:
                    name = TextFormatting.RED + relationship.user.getUsername();
                    break;

                case AWAY:
                    name = TextFormatting.YELLOW + relationship.user.getUsername();
                    break;

                default:
                    name = TextFormatting.DARK_RED + relationship.user.getUsername();
                    break;
            }

            // Username
            this.fr.drawString(Arrays.truncate(name, 22)
                    , x - 100
                    , y + (height - this.fr.FONT_HEIGHT + 1) / 2
                    , 0xFFFFFF);
        }

        @Override
        protected void actionPerformed(GuiButton b)
        {
            // Deleting friend
            if (b.id == 0)
            {
                mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                    if (result)
                        DiscordUtil.deleteFriend(relationship.user.getId());

                    mc.displayGuiScreen(guiFriends);
                }, "", "Are you sure you want to unfriend " + relationship.user.getUsername() + "?", 0));
            }

            // Adding friend
            else if (b.id == 1)
            {
                DiscordUtil.addFriend(relationship.user.getId());
            }

            // Unblock
            else if (b.id == 2)
            {
                DiscordUtil.unblock(relationship.user.getId());
            }
        }
    }

    public class CategoryEntry extends GuiEntry
    {
        private String categoryName;
        private FontRenderer fr;

        public CategoryEntry(FontRenderer fr, String categoryName)
        {
            this.fr = fr;
            this.categoryName = categoryName;
        }

        @Override
        public void drawEntry(int index,int x,int y,int width,int height,int mouseX,int mouseY,boolean isSelected)
        {
            // Username
            this.fr.drawString(categoryName
                    , x + ((width-100)/2)-(fr.getStringWidth(categoryName)/2)
                    , y + (height - this.fr.FONT_HEIGHT + 1) / 2
                    , 0xFFFFFF);
        }
    }
}
