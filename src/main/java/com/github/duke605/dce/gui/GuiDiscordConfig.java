package com.github.duke605.dce.gui;

import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.Reference;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import java.util.Arrays;

public class GuiDiscordConfig extends GuiConfig
{
    public GuiDiscordConfig(GuiScreen parentScreen)
    {
        super(parentScreen
                , Arrays.asList(
                        new ConfigElement(Config.instance.getCategory(Config.CATEGORY_COLOUR)),
                        new ConfigElement(Config.instance.getCategory(Config.CATEGORY_DISPLAY)),
                        new ConfigElement(Config.instance.getCategory(Configuration.CATEGORY_GENERAL)),
                        new ConfigElement(Config.instance.getCategory(Config.CATEGORY_PRIVACY))
                )
                , Reference.MODID
                , false
                , false
                , "DiscordCE Configuration");
    }
}
