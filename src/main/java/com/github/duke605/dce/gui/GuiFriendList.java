package com.github.duke605.dce.gui;

import com.github.duke605.dce.entity.Relationship;
import com.github.duke605.dce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.dce.gui.abstraction.GuiEntry;
import com.github.duke605.dce.gui.abstraction.GuiListButton;
import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.VolatileSettings;
import com.github.duke605.dce.util.*;
import net.dv8tion.jda.entities.User;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


public class GuiFriendList extends GuiEmbeddedList
{
    private GuiFriends guiFriends;
    private List<RelationshipEntry> entries = new ArrayList<>();
    private Minecraft mc;

    public GuiFriendList(Minecraft mc, GuiFriends guiFriends)
    {
        super(mc
                , guiFriends.width + 100
                , guiFriends.height
                , 16 + mc.fontRenderer.FONT_HEIGHT
                , guiFriends.height - (16 + mc.fontRenderer.FONT_HEIGHT)
                , 26);
        this.guiFriends = guiFriends;
        this.mc = mc;
        initList();
    }

    public void initList()
    {
        this.entries.clear();

        if (guiFriends.type == Relationship.FRIEND)
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> r.getValue().type == Relationship.FRIEND
                        && r.getValue().user != null)
                    .map(r -> new RelationshipEntry(mc.fontRenderer, r.getValue()))
                    .collect(Collectors.toList()));

        if (guiFriends.type == Relationship.BLOCK)
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> r.getValue().type == Relationship.BLOCK
                            && r.getValue().user != null)
                    .map(r -> new RelationshipEntry(mc.fontRenderer, r.getValue()))
                    .collect(Collectors.toList()));

        if (guiFriends.type == Relationship.OUTGOING)
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> r.getValue().type == Relationship.OUTGOING
                            && r.getValue().user != null)
                    .map(r -> new RelationshipEntry(mc.fontRenderer, r.getValue()))
                    .collect(Collectors.toList()));

        if (guiFriends.type == Relationship.INCOMING || guiFriends.type == Relationship.OUTGOING)
            this.entries.addAll(VolatileSettings.relationships.entrySet().stream()
                    .filter(r -> (r.getValue().type == Relationship.INCOMING
                                || r.getValue().type == Relationship.OUTGOING)
                            && r.getValue().user != null)
                    .map(r -> new RelationshipEntry(mc.fontRenderer, r.getValue()))
                    .collect(Collectors.toList()));

        // Downloading user avatars
        if (Config.userAvatars)
            entries.forEach(e -> {
                User user = e.relationship.user;

                String url = user.getAvatarId() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl();

                // Checking if the avatar is already loaded or being loaded
                if (VolatileSettings.icons.containsKey(url))
                    return;

                // Placeholder so an image isn't fetched twice
                VolatileSettings.icons.put(url, null);

                Future<BufferedImage> f = ConcurrentUtil.executor.submit(() ->
                        HttpUtil.getImage(url, DrawingUtils::circularize));

                ConcurrentUtil.pushImageTaskToQueue(f, url);
            });
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
        public Relationship relationship;
        private FontRenderer fr;

        public RelationshipEntry(FontRenderer fr, Relationship relationship)
        {
            this.fr = fr;
            this.relationship = relationship;

            // Remove friend
            if (relationship.type == Relationship.FRIEND)
            {
                guiButtons.add(new GuiListButton(0, getListWidth() - 50, 0, 50, 20, "Unfriend"));
                guiButtons.add(new GuiListButton(3, getListWidth() - 105, 0, 50, 20, "DM"));
            }

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
        public void drawEntry(int index, int x, int y, int width, int height,Tessellator t, int mouseX, int mouseY, boolean isSelected)
        {
            for (GuiListButton button : guiButtons)
                button.drawButton(mc, mouseX, mouseY, x, y);

            // Status
            long colour;
            colour = DiscordUtil.getStatusColour(relationship.user.getOnlineStatus());

            // Username
            this.fr.drawString(Arrays.truncate(relationship.user.getUsername(), 20)
                    , x - 100
                    , y + (height - this.fr.FONT_HEIGHT + 1) / 2
                    , 0xFFFFFF);

            int xCoord = fr.getStringWidth(Arrays.truncate(relationship.user.getUsername(), 20));
            DrawingUtils.drawScaledImage(x - 95 + xCoord, y + 8.5F, 0, 0, 10, 10, 0.5F, GuiFriends.indicator
                    , (colour & 0xFF0000) >> 16
                    , (colour & 0x00FF00) >> 8
                    , colour & 0x0000FF
                    , 10, 10);

            // Getting user avatar
            ResourceLocation rl = VolatileSettings.icons.get(relationship.user.getAvatarId() == null
                    ? relationship.user.getDefaultAvatarUrl()
                    : relationship.user.getAvatarUrl());

            ResourceLocation rld = VolatileSettings.icons.get(relationship.user.getDefaultAvatarUrl());

            // Checking if user avatar is loaded
            if (rl == null && rld == null)
                return;

            // Drawing image
            DrawingUtils.drawScaledImage(
                    x - 122
                    ,y + 2
                    ,0, 0, 128, 128, 16/128.0F, rl == null ? rld : rl
                    , 255,255,255, rl == null ? 0.5 : 1 ,128, 128);
        }

        @Override
        protected void actionPerformed(GuiButton b)
        {
            // Deleting friend
            if (b.id == 0)
            {
                String s;
                if (relationship.type == Relationship.INCOMING)
                    s = "Are you sure you want to ignore " + relationship.user.getUsername() + "'s friend request?";
                else if (relationship.type == Relationship.OUTGOING)
                    s = "Are you sure you want to cancel your friend request to " + relationship.user.getUsername() + "?";
                else
                    s = "Are you sure you want to unfriend " + relationship.user.getUsername() + "?";

                mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                    if (result)
                        DiscordUtil.deleteFriend(relationship.user.getId());

                    mc.displayGuiScreen(guiFriends);
                }, "", s, 0));
            }

            // Adding friend
            else if (b.id == 1)
            {
                DiscordUtil.addFriend(relationship.user.getId());
            }

            // Unblock
            else if (b.id == 2)
            {
                mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                    if (result)
                        DiscordUtil.unblock(relationship.user.getId());

                    mc.displayGuiScreen(guiFriends);
                }, "", "Are you sure you want to unblock " + relationship.user.getUsername() + "?", 0));
            }

            // DM
            else if (b.id == 3)
            {
                mc.displayGuiScreen(new GuiChat("/// " + relationship.user.getId() + " "));
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
        public void drawEntry(int index,int x,int y,int width,int height,Tessellator t,int mouseX,int mouseY,boolean isSelected)
        {
            // Username
            this.fr.drawString(categoryName
                    , x + ((width-100)/2)-(fr.getStringWidth(categoryName)/2)
                    , y + (height - this.fr.FONT_HEIGHT + 1) / 2
                    , 0xFFFFFF);
        }
    }
}
