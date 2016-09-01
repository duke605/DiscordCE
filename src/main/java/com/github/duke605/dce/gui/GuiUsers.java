package com.github.duke605.dce.gui;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.dce.gui.abstraction.GuiListContainer;
import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.VolatileSettings;
import com.github.duke605.dce.util.ConcurrentUtil;
import com.github.duke605.dce.util.DiscordUtil;
import com.github.duke605.dce.util.DrawingUtils;
import com.github.duke605.dce.util.HttpUtil;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.minecraft.client.gui.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public class GuiUsers extends GuiListContainer
{
    private GuiUserList userList;
    private GuiScreen parent;

    private GuiButton next;
    private GuiButton prev;
    private GuiButton addRemove;
    private GuiButton blockUnblock;
    private GuiButton dm;
    private GuiButton ignore;

    public List<User> users;
    public List<Guild> guilds;
    public GuiTextField search;

    public GuiUsers(GuiScreen parent)
    {
        this.parent = parent;
        this.users = DiscordCE.client.getUsers();
        this.guilds = DiscordCE.client.getGuilds();

        // Downloading guild icons
        if (Config.guildIcons)
            guilds.forEach(g -> {
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
    public void initGui()
    {
        search = new GuiTextField(mc.fontRenderer, width/2-100, height - 22, 200, 19);
        userList = new GuiUserList(mc, this);
        buttonList.add(new GuiButton(0
                , 10
                , ((fontRendererObj.FONT_HEIGHT + 16)/2) - 9
                , 40
                , 20
                , "< Back"));

        buttonList.add(prev = new GuiButton(-1
                , 10
                , height - 22
                , 40
                , 20
                , "< Prev"));

        buttonList.add(next = new GuiButton(-2
                , width - 50
                , height - 22
                , 40
                , 20
                , "Next >"));

        buttonList.add(addRemove = new GuiButton(1, width/2-101, height - 48, 100, 20, "-"));
        buttonList.add(blockUnblock = new GuiButton(2, width/2+1, height - 48, 100, 20, "-"));
        buttonList.add(ignore = new GuiButton(3, width/2-203, height - 48, 100, 20, "-"));
        buttonList.add(dm = new GuiButton(4, width/2+103, height - 48, 100, 20, "-"));
        prev.enabled = false;
        addRemove.enabled = false;
        blockUnblock.enabled = false;
        dm.enabled = false;
        ignore.enabled = false;
    }

    @Override
    protected void actionPerformed(GuiButton b)
    {
        // Previous page
        if (b.id == -1)
        {
            userList.selectedIdx = -1;
            next.enabled = true;

            if (--userList.page == 0)
                b.enabled = false;

            userList.initList();
        }

        // Next page
        else if (b.id == -2)
        {
            userList.selectedIdx = -1;
            prev.enabled = true;
            userList.page++;

            if (Math.ceil(users.size() / 30) - 1 == userList.page)
                b.enabled = false;

            userList.initList();
        }

        // Back to parent
        else if (b.id == 0)
            mc.displayGuiScreen(parent);

        // Adding removing friend
        else if (b.id == 1)
        {
            User user = userList.entries.get(userList.selectedIdx).user;
            String s = search.getText();
            int selected = userList.selectedIdx;
            int page = userList.page;

            // Removing friend
            if (VolatileSettings.isFriend(user.getId()))
                mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                    if (result)
                        DiscordUtil.deleteFriend(user.getId());

                    mc.displayGuiScreen(this);
                    setState(s, page, selected);
                }, "", "Are you sure you want to unfriend " + user.getUsername() + "?", 0));

            // Canceling friend request
            else if (VolatileSettings.hasOutgoingFriendRequest(user.getId()))
                mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                    if (result)
                        DiscordUtil.deleteFriend(user.getId());

                    mc.displayGuiScreen(this);
                    setState(s, page, selected);
                }, "", "Are you sure you want to cancel your friend request to " + user.getUsername() + "?", 0));

            // Adding friend
            else
                DiscordUtil.addFriend(user.getId());
        }

        // Blocking unblocking user
        else if (b.id == 2)
        {
            User user = userList.entries.get(userList.selectedIdx).user;
            String s = search.getText();
            int page = userList.page;
            int selected = userList.selectedIdx;

            // Unblocking user
            if (VolatileSettings.isBlocked(user.getId()))
                mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                    if (result)
                        DiscordUtil.unblock(user.getId());

                    mc.displayGuiScreen(this);
                    setState(s, page, selected);
                }, "", "Are you sure you want to unblock " + user.getUsername() + "?", 0));

           // Blocking user
            else
                mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                    if (result)
                        DiscordUtil.block(user.getId());

                    mc.displayGuiScreen(this);
                    setState(s, page, selected);
                }, "Are you sure you want to block " + user.getUsername() + "?"
                        , "This will also unfriend them if you are friends.", 0));
        }

        // Ignore friend request
        else if (b.id == 3)
        {
            String s = search.getText();
            int page = userList.page;
            int selected = userList.selectedIdx;
            User user = userList.entries.get(userList.selectedIdx).user;

            // Asking the user if the want to ignore a friend request
            mc.displayGuiScreen(new GuiYesNo((result, id) -> {
                if (result)
                    DiscordUtil.deleteFriend(user.getId());

                mc.displayGuiScreen(this);
                setState(s ,page, selected);
            }, "", "Are you sure you want to ignore " + user.getUsername() + "'s friend request?", 0));
        }

        // DM
        else if (b.id == 4)
            mc.displayGuiScreen(new GuiChat("/// " + userList.entries.get(userList.selectedIdx).user.getId() + " "));
    }

    @Override
    protected void mouseClicked(int x, int y, int u)
    {
        super.mouseClicked(x, y, u);
        search.mouseClicked(x, y, u);
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        userList.drawScreen(x, y, f);
        search.drawTextBox();
        drawCenteredString(mc.fontRenderer, "Users", width / 2, 9, 0xFFFFFFFF);
        super.drawScreen(x, y, f);
    }

    @Override
    public GuiEmbeddedList getEmbeddedList()
    {
        return userList;
    }

    @Override
    protected void keyTyped(char c, int code)
    {
        if(code == 1)
            mc.displayGuiScreen(parent);

        else if (search.isFocused())
        {
            search.textboxKeyTyped(c, code);
            userList.selectedIdx = -1;
            userList.page = 0;
            userList.initList();

            // Enabling buttons
            if (search.getText().isEmpty())
            {
                next.enabled = true;
                prev.enabled = true;
            }

            // Disabling buttons
            else
            {
                next.enabled = false;
                prev.enabled = false;
            }
        }
    }

    @Override
    public void updateScreen()
    {
        search.updateCursorCounter();

        // Enabling buttons
        if (userList.selectedIdx != -1)
        {
            addRemove.enabled = true;
            blockUnblock.enabled = true;
            ignore.enabled = false;
            ignore.displayString = "-";

            User user = userList.entries.get(userList.selectedIdx).user;

            // Setting add remove to remove friend
            if (VolatileSettings.isFriend(user.getId()))
                addRemove.displayString = "Remove Friend";

            // Setting add remove to cancel
            else if (VolatileSettings.hasIncomingFriendRequest(user.getId()))
            {
                addRemove.displayString = "Accept Request";
                ignore.displayString = "Ignore Request";
                ignore.enabled = true;
            }

            // Setting add remove to cancel
            else if (VolatileSettings.hasOutgoingFriendRequest(user.getId()))
                addRemove.displayString = "Cancel Request";

            // Setting add remove to remove friend
            else
                addRemove.displayString = "Add Friend";


            // Setting block unblock to unblock
            if (VolatileSettings.isBlocked(user.getId()))
            {
                dm.displayString = "-";
                dm.enabled = false;
                blockUnblock.displayString = "Unblock";
            }

            // Setting block unblock to block
            else
            {
                dm.displayString = "Direct Message";
                dm.enabled = true;
                blockUnblock.displayString = "Block";
            }


        }

        // Disabling buttons
        else
        {
            addRemove.enabled = false;
            blockUnblock.enabled = false;
            dm.enabled = false;
            ignore.enabled = false;
            addRemove.displayString = "-";
            blockUnblock.displayString = "-";
            dm.displayString = "-";
            ignore.displayString = "-";
        }
    }

    private void setState(String s, int page, int selected)
    {
        search.setFocused(true);
        for(char c : s.toCharArray())
            try { keyTyped(c, (int) c); } catch (Exception e) {}
        userList.page = page;
        userList.selectedIdx = selected;
    }
}
