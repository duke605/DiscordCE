package com.github.duke605.discordce.command;

import com.github.duke605.discordce.util.DiscordUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class CommandDeleteFriend extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "deleteFriend";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.singletonList("deletefriend");
    }

    @Override
    public boolean checkPermission(MinecraftServer s, ICommandSender ss)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender ss)
    {
        return "deleteFriend <userId>";
    }

    @Override
    public void execute(MinecraftServer s,
                        ICommandSender ss,
                        String[] args) throws CommandException
    {
        DiscordUtil.deleteFriend(args[0]);
    }
}
