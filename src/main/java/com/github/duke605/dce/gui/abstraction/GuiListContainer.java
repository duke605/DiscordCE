package com.github.duke605.dce.gui.abstraction;

import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public abstract class GuiListContainer extends GuiScreen
{

    public abstract GuiEmbeddedList getEmbeddedList();

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        this.getEmbeddedList().
    }

    @Override
    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_)
    {
        if(p_mouseClicked_3_ != 0 || !this.getEmbeddedList().mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)) {
            super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
        }
    }
}
