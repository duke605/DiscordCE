package com.github.duke605.dce.gui.abstraction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.json.JSONObject;

public class GuiListButton extends GuiButton
{
    private int xRelative;
    private int yRelative;

    public GuiListButton(int id, int x, int y, String text)
    {
        super(id, x, y, text);
        xRelative = x;
        yRelative = y;
    }

    public GuiListButton(int id, int x, int y, int width, int height, String text)
    {
        super(id, x, y, width, height, text);
        xRelative = x;
        yRelative = y;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, int x, int y)
    {
        this.xPosition = xRelative + x;
        this.yPosition = yRelative + y;
        super.drawButton(mc, mouseX, mouseY);
    }
}
