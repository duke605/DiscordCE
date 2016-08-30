package com.github.duke605.dce.command;

import com.github.duke605.dce.util.DiscordUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class CommandSwitchChannel extends CommandBase
{
    @Override
    public String getCommandName() {
        return "switchChannel";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.singletonList("switchchannel");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "switchChannel <channelId>";
    }

    @Override
    public boolean func_71519_b(ICommandSender ss) {
        return true;
    }

    @Override
    public void func_71515_b(ICommandSender ss, String[] args) throws CommandException {
        DiscordUtil.switchToChannel(args);
    }
}
