package com.github.duke605.dce.gui.factory;

import com.github.duke605.dce.gui.GuiDiscordConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class GuiFactoryDiscordConfig implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraft)
    {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return GuiDiscordConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement runtimeOptionCategoryElement)
    {
        return null;
    }
}
