package com.github.duke605.dce.gui;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.contract.CustomListenerAdapter;
import com.github.duke605.dce.entity.Relationship;
import com.github.duke605.dce.event.RelationshipAddEvent;
import com.github.duke605.dce.event.RelationshipRemoveEvent;
import com.github.duke605.dce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.dce.gui.abstraction.GuiListContainer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiFriends extends GuiListContainer
{

    private GuiScreen parent;
    private CustomListenerAdapter listener;
    private GuiFriendList friendList;
    int type = Relationship.FRIEND;
    static ResourceLocation indicator = new ResourceLocation("dce:textures/gui/status_dot.png");

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
        GuiButton b;
        buttonList.add(new GuiButton(-1
                , 10
                , ((fontRendererObj.FONT_HEIGHT + 16)/2) - 9
                , 40
                , 20
                , "< Back"));

        buttonList.add(b = new GuiButton(Relationship.FRIEND
                , width / 2 - 77
                , height - 23
                , 50
                , 20
                , "Friends"));
        b.enabled = false;

        buttonList.add(new GuiButton(3
                , width / 2 - 25
                , height - 23
                , 50
                , 20
                , "Pending"));

        buttonList.add(new GuiButton(Relationship.BLOCK
                , width / 2 + 27
                , height - 23
                , 50
                , 20
                , "Blocked"));
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        getEmbeddedList().drawScreen(x, y, f);
        drawCenteredString(mc.fontRendererObj, "Relationships", width / 2, 9, 0xFFFFFFFF);

        super.drawScreen(x, y, f);
    }

    @Override
    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) throws IOException
    {
        if(p_keyTyped_2_ == 1)
            this.mc.displayGuiScreen(parent);
    }

    @Override
    protected void actionPerformed(GuiButton b) throws IOException
    {
        // Back
        if (b.id == -1)
        {
            mc.displayGuiScreen(parent);
            return;
        }

        b.enabled = false;
        buttonList.stream().filter(button -> button.id > 0 && button != b).forEach(button -> button.enabled = true);
        type = b.id;
        friendList.initList();
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
