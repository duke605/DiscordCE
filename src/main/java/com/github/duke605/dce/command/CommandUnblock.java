package com.github.duke605.dce.command;

import com.github.duke605.dce.util.DiscordUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Created by Cole on 8/20/2016.
 */
public class CommandUnblock extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "unblock";
    }

    @Override
    public boolean func_71519_b(ICommandSender ss)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender ss)
    {
        return "unblock <userId>";
    }

    @Override
    public void func_71515_b(ICommandSender ss, String[] args) throws CommandException
    {
        DiscordUtil.unblock(args[0]);
    }
}
