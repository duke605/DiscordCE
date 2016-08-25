package com.github.duke605.discordce.gui;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.contract.CustomListenerAdapter;
import com.github.duke605.discordce.event.RelationshipAddEvent;
import com.github.duke605.discordce.event.RelationshipRemoveEvent;
import com.github.duke605.discordce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.discordce.gui.abstraction.GuiListContainer;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiFriends extends GuiListContainer
{

    private GuiScreen parent;
    private CustomListenerAdapter listener;
    private GuiFriendList friendList;

    public GuiFriends(GuiScreen parent)
    {
        this.parent = parent;
        listener = new CustomListenerAdapter()
        {
            @Override
            public void onRelationshipAdd(RelationshipAddEvent e)
            {
                friendList.initList();
            }

            @Override
            public void onRelationshipRemove(RelationshipRemoveEvent e)
            {
                friendList.initList();
            }
        };
    }

    @Override
    public void initGui()
    {
        friendList = new GuiFriendList(mc, this);
        DiscordCE.client.removeEventListener(listener);
        DiscordCE.client.addEventListener(listener);
        buttonList.clear();
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        getEmbeddedList().drawScreen(x, y, f);
        drawCenteredString(mc.fontRendererObj, "Friends List", width / 2, 8, 0xFFFFFFFF);

        super.drawScreen(x, y, f);
    }

    @Override
    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) throws IOException
    {
        if(p_keyTyped_2_ == 1)
            this.mc.displayGuiScreen(parent);
    }

    @Override
    public void onGuiClosed()
    {
        DiscordCE.client.removeEventListener(listener);
    }

    @Override
    public GuiEmbeddedList getEmbeddedList()
    {
        return friendList;
    }
}
