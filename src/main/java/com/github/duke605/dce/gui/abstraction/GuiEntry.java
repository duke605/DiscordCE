package com.github.duke605.dce.gui.abstraction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cole on 8/25/2016.
 */
public abstract class GuiEntry implements GuiListExtended.IGuiListEntry
{
    protected List<GuiListButton> guiButtons = new ArrayList<>();

    @Override
    public abstract void drawEntry(int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean isSelected);

    @Override
    public boolean mousePressed(int var1, int mouseX, int mouseY, int var4, int var5, int var6)
    {
        for(GuiListButton button : guiButtons)
        {
            if (button.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                actionPerformed(button);
                return true;
            }
        }
        return false;
    }

    protected void actionPerformed(GuiButton b) {}

    @Override
    public void mouseReleased(int i, int i1, int i2, int i3, int i4, int i5) {}

    @Override
    public void setSelected(int i, int i1, int i2) {}
}
