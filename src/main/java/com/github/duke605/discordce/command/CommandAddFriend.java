package com.github.duke605.discordce.command;

import com.github.duke605.discordce.util.DiscordUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class CommandAddFriend extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "addFriend";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.singletonList("addfriend");
    }

    @Override
    public boolean checkPermission(MinecraftServer s, ICommandSender ss)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender ss)
    {
        return "addFriend <userId>";
    }

    @Override
    public void execute(MinecraftServer s,
                        ICommandSender ss,
                        String[] args) throws CommandException
    {
        DiscordUtil.addFriend(args[0]);
    }
}
