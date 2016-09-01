package com.github.duke605.dce.command;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.lib.Preferences;
import com.github.duke605.dce.util.Arrays;
import com.github.duke605.dce.util.MCHelper;
import net.dv8tion.jda.client.JDAClient;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.exceptions.PermissionException;
import net.dv8tion.jda.exceptions.VerificationLevelException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandChat extends CommandBase
{
    @Override
    public String getCommandName() {
        return "/";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "// <message>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String message = Arrays.join(args, " ");
        String channelId = Preferences.i.usingChannel;
        JDAClient c = DiscordCE.client;
        User self = c.getUserById(c.getSelfInfo().getId());

        // No channel
        if (channelId == null) {
            MCHelper.sendMessage("\u00a7cYou must specify a channel to send messages to in the configuration GUI.");
            return;
        }

        // Checking if channel still exists
        if (DiscordCE.client.getTextChannelById(channelId) == null) {
            MCHelper.sendMessage("\u00a7cThe channel you specified to send messages to no longer exists.");
            return;
        }

        // Checking if user can send messages to the channel
        try {
            c.getTextChannelById(channelId).sendMessageAsync(message, null);
        }
        catch (PermissionException e)
        {
            MCHelper.sendMessage("\u00a7cYou do not have permission to send messages to this channel.");
        }
        catch (VerificationLevelException e)
        {
            MCHelper.sendMessage("\u00a7c" + e.getMessage());
        }
    }
}
