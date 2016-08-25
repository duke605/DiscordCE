package com.github.duke605.discordce.command;

import com.github.duke605.discordce.util.DiscordUtil;
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
    public boolean checkPermission(MinecraftServer s, ICommandSender ss)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender ss)
    {
        return "unblock <userId>";
    }

    @Override
    public void execute(MinecraftServer s,
                        ICommandSender ss,
                        String[] args) throws CommandException
    {
        DiscordUtil.unblock(args[0]);
    }
}
