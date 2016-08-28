package com.github.duke605.dce.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class GuiDiscordCeMenu extends GuiScreen
{
    @Override
    public void initGui()
    {
        buttonList.clear();
        this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.format("menu.returnToGame")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, "Guilds"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, "Relationships"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 72 + -16, 98, 20, "Configurations"));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 2, this.height / 4 + 72 + -16, 98, 20, "Users"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 96 + -16, "Report an Issue"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    protected void actionPerformed(GuiButton b) throws IOException
    {

        // Exiting gui
        if (b.id == -1)
            mc.displayGuiScreen(null);

        // Showing guilds in
        else if (b.id == 0)
            mc.displayGuiScreen(new GuiServers(this));

        // Showing relationships
        else if (b.id == 1)
            mc.displayGuiScreen(new GuiFriends(this));

        // Showing instance
        else if (b.id == 2)
        {
            ModContainer container = Loader.instance().getIndexedModList().get("dce");
            try {
                IModGuiFactory e1 = FMLClientHandler.instance().getGuiFactoryFor(container);
                GuiScreen newScreen1 = e1
                        .mainConfigGuiClass()
                        .getConstructor(new Class[]{GuiScreen.class})
                        .newInstance(this);
                this.mc.displayGuiScreen(newScreen1);
            } catch (Exception var5) {
                FMLLog.log(Level.ERROR, var5, "There was a critical issue trying to build the instance GUI for %s",
                        container.getModId());
            }
        }

        // Showing user list
        else if (b.id == 3)
            mc.displayGuiScreen(new GuiUsers(this));

        // Report issue
        else if (b.id == 5)
            Desktop.getDesktop().browse(URI.create("https://github.com/duke605/DiscordCE/issues"));
    }
}
