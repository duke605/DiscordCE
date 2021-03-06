package com.github.duke605.dce.command;

import com.github.duke605.dce.lib.Preferences;
import com.github.duke605.dce.util.MCHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandFocus extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "focus";
    }

    @Override
    public String getCommandUsage(ICommandSender s)
    {
        return "focus";
    }

    @Override
    public boolean checkPermission(MinecraftServer s, ICommandSender ss)
    {
        return true;
    }

    @Override
    public void execute(MinecraftServer s,
                        ICommandSender ss,
                        String[] args) throws CommandException
    {
        // toggling focus
        Preferences.i.focus = !Preferences.i.focus;
        Preferences.save();
        MCHelper.sendMessage("Focus is now " + (Preferences.i.focus ? "\u00a7aON." : "\u00a7cOFF."));
    }
}
