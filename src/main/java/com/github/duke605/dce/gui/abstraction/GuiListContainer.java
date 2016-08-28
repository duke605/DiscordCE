package com.github.duke605.dce.gui.abstraction;

import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public abstract class GuiListContainer extends GuiScreen
{

    public abstract GuiEmbeddedList getEmbeddedList();

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.getEmbeddedList().handleMouseInput();
    }

    protected void mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_2_, int p_mouseReleased_3_)
    {
        if(p_mouseReleased_3_ != 0 || !this.getEmbeddedList().mouseReleased(p_mouseReleased_1_, p_mouseReleased_2_, p_mouseReleased_3_)) {
            super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_2_, p_mouseReleased_3_);
        }
    }

    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_) throws IOException
    {
        if(p_mouseClicked_3_ != 0 || !this.getEmbeddedList().mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)) {
            super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
        }
    }
}
