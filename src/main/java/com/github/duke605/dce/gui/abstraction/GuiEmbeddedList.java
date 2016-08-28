package com.github.duke605.dce.gui.abstraction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

public abstract class GuiEmbeddedList extends GuiListExtended
{
    public GuiEmbeddedList(Minecraft p_i45010_1_,
                           int p_i45010_2_,
                           int p_i45010_3_,
                           int p_i45010_4_,
                           int p_i45010_5_, int p_i45010_6_)
    {
        super(p_i45010_1_, p_i45010_2_, p_i45010_3_, p_i45010_4_, p_i45010_5_, p_i45010_6_);
    }

    @Override
    public abstract IGuiListEntry getListEntry(int i);

    @Override
    protected abstract int getSize();
}
