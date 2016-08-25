package com.github.duke605.discordce.command;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.lib.Preferences;
import com.github.duke605.discordce.util.Arrays;
import com.github.duke605.discordce.util.MCHelper;
import com.github.duke605.discordce.util.NumberUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandListChannels extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "listChannels";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.singletonList("listchannels");
    }

    @Override
    public boolean checkPermission(MinecraftServer s, ICommandSender ss)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender ss)
    {
        return "listChannels <page> <channelNamePartial>";
    }

    @Override
    public void execute(MinecraftServer s,
                        ICommandSender ss,
                        String[] args) throws CommandException
    {
        if (args.length < 1
                || !NumberUtil.isInt(args[0])
                || Integer.parseInt(args[0]) < 1)
        {
            MCHelper.sendMessage("§cUsage: " + getCommandUsage(ss) + "\n" +
                    "   §cPage number must cannot be lower than 1.");
            return;
        }

        int page = Integer.parseInt(args[0]) - 1;
        final String channelPartial = Arrays.join(java.util.Arrays.copyOfRange(args, 1, args.length), " ");
        final User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());
        long count = DiscordCE.client.getTextChannels().stream()
                .sorted((c, c1) ->
                        c.getName().compareTo(c1.getName()))
                .filter(c ->
                        c.getName().toLowerCase().startsWith(channelPartial.toLowerCase())
                                && c.checkPermission(me, Permission.MESSAGE_READ)
                                && c.checkPermission(me, Permission.MESSAGE_WRITE))
                .count();
        int totalPages = (int) Math.ceil(count / 5);
        page = Math.min(totalPages, page);
        Set<Map.Entry<Guild, List<TextChannel>>> channels = DiscordCE.client.getTextChannels().stream()
                .sorted((c, c1) ->
                        c.getName().compareTo(c1.getName()))
                .filter(c ->
                        c.getName().toLowerCase().startsWith(channelPartial.toLowerCase())
                                && c.checkPermission(me, Permission.MESSAGE_READ)
                                && c.checkPermission(me, Permission.MESSAGE_WRITE))
                .skip(page*5)
                .limit(5)
                .collect(Collectors.groupingBy(Channel::getGuild))
                .entrySet();

        // No servers found
        if (channels.size() == 0)
        {
            MCHelper.sendMessage("§cNo channels that begin with \"" + channelPartial + "\" could be found.");
            return;
        }

        // Creating output message
        final JSONArray message = new JSONArray().put("");
        channels.forEach(x ->
        {
            String sName = x.getKey().getId().equals(Preferences.i.usingGuild)
                    ? x.getKey().getName() + " ★"
                    : x.getKey().getName();

            // Putting server name as header
            message.put(MCHelper.serverAsRaw(x.getKey().getId(), sName));
            message.put("\n");

            // Subchannels
            x.getValue().forEach(c ->
            {
                String cName = c.getId().equals(Preferences.i.usingChannel)
                        ? c.getName() + " ★"
                        : c.getName();

                message.put(MCHelper.channelAsRaw(c.getId(), "   #" + cName));
                message.put("\n");
            });
        });

        // Adding pagination

        // Back
        if (page + 1 > 1)
            message.put(new JSONObject()
                .put("text", "§b<<§f")
                .put("clickEvent", new JSONObject()
                    .put("action", "run_command")
                    .put("value", "/listChannels " + page + " " + channelPartial)));
        else
            message.put("<<");

        // out of pages
        message.put(" (" + (page + 1) + "/" + totalPages + ") ");

        if (page + 1 < totalPages)
            message.put(new JSONObject()
                    .put("text", "§b>>§f")
                    .put("clickEvent", new JSONObject()
                            .put("action", "run_command")
                            .put("value", "/listChannels " + (page + 2) + " " + channelPartial)));
        else
            message.put(">>");

        // Sending the raw message to user
        MCHelper.sendRawMessage(message);
    }
}
