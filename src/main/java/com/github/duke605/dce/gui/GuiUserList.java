package com.github.duke605.dce.gui;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.dce.gui.abstraction.GuiEntry;
import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.VolatileSettings;
import com.github.duke605.dce.util.*;
import net.dv8tion.jda.entities.User;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GuiUserList extends GuiEmbeddedList
{
    private GuiUsers guiUsers;
    public List<UserEntry> entries = new ArrayList<>();
    public int selectedIdx = -1;
    public int page = 0;

    public GuiUserList(Minecraft mc, GuiUsers guiUsers)
    {
        super(mc
                , guiUsers.width
                , guiUsers.height
                , 16 + mc.fontRendererObj.FONT_HEIGHT
                , guiUsers.height - 50
                , mc.fontRendererObj.FONT_HEIGHT + 16);
        this.guiUsers = guiUsers;
        initList();
    }

    public void initList()
    {
        final int MAX = 30;


        entries = guiUsers.users.parallelStream()
                .sorted((u, u2) -> {
                    String search = guiUsers.search.getText().trim();
                    if (!search.isEmpty())
                    {
                        double dist = StringUtils.getJaroWinklerDistance(u.getUsername(), search);
                        double dist2 = StringUtils.getJaroWinklerDistance(u2.getUsername(), search);
                        return Double.compare(dist2, dist);
                    }

                    if (u.getUsername().toLowerCase().equals(u2.getUsername().toLowerCase()))
                        return u.getId().compareTo(u2.getId());

                    return u.getUsername().toLowerCase().compareTo(u2.getUsername().toLowerCase());
                })
                .filter(u -> {
                    String search = guiUsers.search.getText().trim();
                    User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());

                    // Exempting ME
                    if (u.getId().equals(me.getId()))
                        return false;

                    // Only filtering if search
                    if (search.isEmpty())
                        return true;

                    double dist = StringUtils.getJaroWinklerDistance(u.getUsername(), search);
                    return dist >= 0.8 || u.getUsername().toLowerCase().startsWith(search.toLowerCase());
                })
                .skip(page * MAX)
                .limit(MAX)
                .map(UserEntry::new)
                .collect(Collectors.toList());

        // Downloading user avatars
        if (Config.userAvatars)
            entries.forEach(u -> {
                User user = u.user;

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

    public class UserEntry extends GuiEntry
    {
        public User user;
        private FontRenderer fr;

        public UserEntry(User user)
        {
            this.user = user;
            this.fr = mc.fontRendererObj;
        }

        @Override
        public void drawEntry(int index,int x,int y,int width,int height,int mouseX,int mouseY,boolean isSelected)
        {
            if (index == selectedIdx)
            {
                DrawingUtils.drawRoundedRect(x, y, width, height, 0xFFFFFFFF, "");
                DrawingUtils.drawRoundedRect(x+1, y+1, width-2, height-2, 0xff000000, "");
            }

            // Getting colour for user's status
            long colour = DiscordUtil.getStatusColour(user.getOnlineStatus());

            // Username
            String name = user.getUsername();

            // Coloring username
            if (user.getId().equals("136856172203474944"))
                name = TextFormatting.YELLOW + name;
            else if (VolatileSettings.isFriend(user.getId()))
                name = "\u00a7" + Config.friendColour + name;

            // Drawing username
            this.fr.drawString(Arrays.truncate(name, 22)
                    , x + 3
                    , y + (height - this.fr.FONT_HEIGHT + 2) / 2
                    , 0xFFFFFF);

            // Drawing online status
            int xCoord = fr.getStringWidth(Arrays.truncate(name, 22));
            DrawingUtils.drawScaledImage(x + 5 + xCoord, y + 8.2F, 0, 0, 10, 10, 0.5F, GuiFriends.indicator
                    , (colour & 0xFF0000) >> 16
                    , (colour & 0x00FF00) >> 8
                    , colour & 0x0000FF
                    , 10, 10);

            // Drawing bot tag if bot
            if (user.isBot())
                DrawingUtils.drawRoundedRect(x + xCoord + 15
                        , y + 6
                        , DrawingUtils.getUnicodeStringWidth("Bot") + 4
                        , 9
                        , 0xFF0000FF
                        , "Bot");

            // Finding common guilds
            AtomicInteger count = new AtomicInteger(0);

            // Drawing guilds user is in
            if (Config.guildIcons)
                guiUsers.guilds.forEach(g -> {
                    // Getting icon for guild
                    ResourceLocation rl = VolatileSettings.icons.get(g.getIconUrl());

                    // Checking if guild has icon
                    if (rl == null)
                    {
                        count.incrementAndGet();
                        return;
                    }

                    // Checking if user is in current guild
                    if (!g.getUsers().contains(user))
                        return;

                    // Drawing image
                    DrawingUtils.drawScaledImage(
                            (x + width) - 18 - (count.getAndIncrement() * 20)
                            ,y + 2
                            ,0, 0, 128, 128, 16/128.0F, rl, 255,255,255, 128, 128);
                });

            // Getting user avatar
            ResourceLocation rl = VolatileSettings.icons.get(user.getAvatarId() == null
                ? user.getDefaultAvatarUrl()
                : user.getAvatarUrl());

            ResourceLocation rld = VolatileSettings.icons.get(user.getDefaultAvatarUrl());

            // Checking if user avatar is loaded
            if (rl == null && rld == null)
                return;

            // Drawing image
            DrawingUtils.drawScaledImage(
                    x - 22
                    ,y + 2
                    ,0, 0, 128, 128, 16/128.0F, rl == null ? rld : rl
                    , 255,255,255, rl == null ? 0.5 : 1 ,128, 128);
        }

        @Override
        public boolean mousePressed(int var1, int mouseX, int mouseY, int var4, int var5, int var6)
        {
            if (super.mousePressed(var1, mouseX, mouseY, var4, var5, var6))
                return true;

            if (!user.isBot())
                selectedIdx = var1;
            return false;
        }
    }
}
