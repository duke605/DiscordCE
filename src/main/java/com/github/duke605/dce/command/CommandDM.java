package com.github.duke605.dce.command;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.util.Arrays;
import com.github.duke605.dce.util.MCHelper;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.exceptions.PermissionException;
import net.dv8tion.jda.exceptions.VerificationLevelException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Created by Cole on 8/26/2016.
 */
public class CommandDM extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "//";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender)
    {
        return "/// <user_id> <message>";
    }

    @Override
    public void execute(MinecraftServer s,
                        ICommandSender ss,
                        String[] args) throws CommandException
    {
        String userId = args[0];
        String message = Arrays.join(java.util.Arrays.copyOfRange(args, 1, args.length), " ");
        User user = DiscordCE.client.getUserById(userId);

        // checking to see if user exists
        if (user == null)
        {
            MCHelper.sendMessage("§cThe user specified to send messages to does not exist.");
            return;
        }

        try {
            user.getPrivateChannel().sendMessageAsync(message, null);
        }
        catch (PermissionException e)
        {
            MCHelper.sendMessage("§cYou do not have permission to send messages to this channel.");
        }
        catch (VerificationLevelException e)
        {
            MCHelper.sendMessage("§c" + e.getMessage());
        }
    }
}
