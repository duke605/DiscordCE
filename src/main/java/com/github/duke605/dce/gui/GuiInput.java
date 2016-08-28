package com.github.duke605.dce.gui;

import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.function.Consumer;

public class GuiInput extends GuiScreen
{
    private Consumer<String> callback;
    private String line1;
    private String line2;
    private GuiTextField textField;

    public GuiInput(Consumer<String> callback, String line1, String line2)
    {
        this.callback = callback;
        this.line1 = line1;
        this.line2 = line2;
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        buttonList.add(new GuiButton(1
                , (width / 2) - 50
                , (height / 2) + 20
                , 100
                , 20
                , "Done"));

        textField = new GuiTextField(0
                , fontRendererObj
                , (width / 2) - 150
                , (height / 2) - 10
                , 300
                , 20);

        textField.setMaxStringLength(2000);
        textField.setFocused(true);
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_)
    {
        super.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, this.line1, this.width / 2, 70, 16777215);
        drawCenteredString(this.fontRendererObj, this.line2, this.width / 2, 80 + fontRendererObj.FONT_HEIGHT, 16777215);
        textField.drawTextBox();

        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    @Override
    protected void keyTyped(char c, int code) throws IOException
    {
        if (code == Keyboard.KEY_ESCAPE)
            super.keyTyped(c, code);
        else if (code == Keyboard.KEY_RETURN)
            actionPerformed(buttonList.get(0));
        else
            textField.textboxKeyTyped(c, code);
    }

    @Override
    protected void actionPerformed(GuiButton b) throws IOException
    {
        callback.accept(textField.getText());
    }

    @Override
    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_) throws IOException
    {
        super.mouseClicked(p_mouseClicked_1_,p_mouseClicked_2_,p_mouseClicked_3_);
        textField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
    }

    @Override
    public void updateScreen()
    {
        textField.updateCursorCounter();
    }
}
