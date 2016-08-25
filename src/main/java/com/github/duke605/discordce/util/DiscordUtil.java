package com.github.duke605.discordce.util;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.lib.Config;
import com.github.duke605.discordce.lib.Preferences;
import com.github.duke605.discordce.lib.VolatileSettings;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import net.dv8tion.jda.client.JDAClient;
import net.dv8tion.jda.entities.*;
import net.dv8tion.jda.requests.Requester;

import java.util.regex.Pattern;

public class DiscordUtil
{
    /**
     * Highlights any mentions in the message
     *
     * @param s The message
     * @return a string with the mentions wrapped with colours
     */
    public static String resolveMentions(String s)
    {
        final JDAClient client = DiscordCE.client;
        s = s.replaceAll("  ", "");

        // Patterns
        Pattern channelPattern = Pattern.compile("<#(\\d+?)>");
        Pattern userPattern = Pattern.compile("<@!?(\\d+?)>");
        Pattern boldPattern = Pattern.compile("(\\*\\*)(.+?)(\\*\\*)");
        Pattern italicPattern = Pattern.compile("(\\*)(.+?)(\\*)");
        Pattern italic2Pattern = Pattern.compile("(_)(.+?)(_)");
        Pattern underlinePattern = Pattern.compile("(__)(.+?)(__)");

        // Finding all channel mentions
        s = RegexUtil.replaceAll(channelPattern, s, "§%s§r", m -> {
            String channelId = m.group(1);
            TextChannel c = client.getTextChannelById(channelId);

            if (c == null)
                return new Object[]{"c#UNKNOWN"};
            else
                return new Object[]{Config.mentionColour + "#" + c.getName()};
        });

        // Finding all user mentions
        s = RegexUtil.replaceAll(userPattern, s, "§%s§r", m -> {
            String userId = m.group(1);
            User u = client.getUserById(userId);

            if (u == null)
                return new Object[]{"c@UNKNOWN"};
            else
                return new Object[]{Config.mentionColour + "@" + u.getUsername()};
        });

        // Finding bold
        s = RegexUtil.replaceAll(boldPattern, s, "§l$2§r", m -> null);

        // Finding italic
        s = RegexUtil.replaceAll(italicPattern, s, "§o$2§r", m -> null);

        // Finding underline
        s = RegexUtil.replaceAll(underlinePattern, s, "§n$2§r", m -> null);

        // Finding italic2
        s = RegexUtil.replaceAll(italic2Pattern, s, "§o$2§r", m -> null);

        return s;
    }

    /**
     * Changes the passed server name to something the user wants
     *
     * @param originalName The name of the server
     * @return the custom server name or the original server name if no override exists.
     */
    public static String overrideServerName(String originalName)
    {
        if (!Config.serverOverrides.containsKey(originalName))
            return originalName;

        return Config.serverOverrides.get(originalName);
    }

    /**
     * Changes the passed channel name to something the user wants
     *
     * @param originalName The name of the channel
     * @return the custom channel name or the original channel name if no override exists.
     */
    public static String overrideChannelName(String originalName)
    {
        if (!Config.channelOverrides.containsKey(originalName))
            return originalName;

        return Config.channelOverrides.get(originalName);
    }

    public static void switchToChannel(String[] args)
    {
        String channelId = args[0];
        Channel channel = DiscordCE.client.getTextChannelById(channelId);

        // Checking if channel exists
        if (channel == null) {
            MCHelper.sendMessage("§cThe with the is of \"§l" + channelId + "§r§c\" does not exist");
            return;
        }

        // Checking if channel switched to is same
        if (channelId.equals(Preferences.i.usingChannel)) {
            MCHelper.sendMessage("§cYou are already on that channel.");
            return;
        }

        // Checking if the channel being switched to is ignored
        if (Preferences.i.mutedChannels.contains(channelId))
            Preferences.i.mutedChannels.remove(channelId);

        // Checking if the server being switched to is ignored
        if (Preferences.i.mutedGuilds.contains(channel.getGuild().getId()))
            Preferences.i.mutedGuilds.remove(channel.getGuild().getId());

        Preferences.i.usingChannel = channelId;
        Preferences.i.usingGuild = channel.getGuild().getId();
        Preferences.save();

        MCHelper.sendMessage("Switching to §" + Config.mentionColour + "#" +channel.getName());
    }

    public static void deleteFriend(String userId)
    {
        final User user = DiscordCE.client.getUserById(userId);

        // Checking if user exists
        if (user == null)
        {
            MCHelper.sendMessage("§cThe user you specified to unfriend does not exist.");
            return;
        }

        // Checking if the user is already blocked
        if (!VolatileSettings.isFriend(userId) && !VolatileSettings.hasOutgoingFriendRequest(userId))
        {
            MCHelper.sendMessage("§cThe user you specified to unfriend is not a friend and does not have a pending " +
                    "friend request.");
            return;
        }

        ConcurrentUtil.executor.execute(() -> {
            try
            {
                HttpResponse<String> r = Unirest.delete(Requester.DISCORD_API_PREFIX + "users/@me/relationships/{userId}")
                        .routeParam("userId", userId)
                        .header("authorization", DiscordCE.client.getAuthToken())
                        .header("user-agent", Requester.USER_AGENT)
                        .asString();

                // Checking if request was ok
                if (r.getStatus() <= 199 || r.getStatus() >= 300)
                {
                    if (r.getStatus() == 429)
                        MCHelper.sendMessage("§cRate limit was reached when attempting to unfriend §9@" + user.getUsername() +
                                ". §cPlease try again in a moment.");
                    else
                        MCHelper.sendMessage("§cAn error occurred when attempting to unfriend §9@" + user.getUsername() +
                                ". §cStatus code: " + r.getStatus() + " (" + r.getStatusText() + ")");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public static void addFriend(String userId)
    {
        final User user = DiscordCE.client.getUserById(userId);
        final String body;

        // Checking if user exists
        if (user == null)
        {
            MCHelper.sendMessage("§cThe user you specified to befriend does not exist.");
            return;
        }

        // Checking if the user is already blocked
        if (VolatileSettings.isFriend(userId))
        {
            MCHelper.sendMessage("§cThe user you specified to befriend is already a friend.");
            return;
        }

        // Checking if there is already an outgoing friend request
        if (VolatileSettings.hasOutgoingFriendRequest(userId))
        {
            MCHelper.sendMessage("§cYou have already sent a friend request to that user.");
            return;
        }

        ConcurrentUtil.executor.execute(() -> {
            try
            {
                HttpResponse<JsonNode> r = Unirest.put(Requester.DISCORD_API_PREFIX+"users/@me/relationships/{userId}")
                        .routeParam("userId", userId)
                        .header("authorization", DiscordCE.client.getAuthToken())
                        .header("user-agent", Requester.USER_AGENT)
                        .header("Content-Type", "application/json")
                        .body("{}")
                        .asJson();

                // Checking if request was ok
                if (r.getStatus() <= 199 || r.getStatus() >= 300)
                {
                    if (r.getStatus() == 429)
                        MCHelper.sendMessage("§cRate limit was reached when attempting to befriend §9@" + user.getUsername() +
                                ". §cPlease try again in a moment.");
                    else
                        MCHelper.sendMessage("§cAn error occurred when attempting to befriend §9@" + user.getUsername() +
                                ". §cStatus code: " + r.getStatus() + " (" + r.getStatusText() + ")");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public static void block(String userId)
    {
        final User user = DiscordCE.client.getUserById(userId);

        // Checking if user exists
        if (user == null)
        {
            MCHelper.sendMessage("§cThe user you specified to block does not exist.");
            return;
        }

        // Checking if the user is already blocked
        if (VolatileSettings.isBlocked(userId))
        {
            MCHelper.sendMessage("§cThe user you specified to block is already blocked.");
            return;
        }

        ConcurrentUtil.executor.execute(() -> {
            try
            {
                HttpResponse<String> r = Unirest.put(Requester.DISCORD_API_PREFIX + "users/@me/relationships/{userId}")
                        .routeParam("userId", userId)
                        .header("authorization", DiscordCE.client.getAuthToken())
                        .header("user-agent", Requester.USER_AGENT)
                        .header("Content-Type", "application/json")
                        .body("{\"type\":2}")
                        .asString();

                // Checking if request was ok
                if (r.getStatus() <= 199 || r.getStatus() >= 300)
                {
                    if (r.getStatus() == 429)
                        MCHelper.sendMessage("§cRate limit was reached when attempting to block §9@" + user.getUsername() +
                                ". §cPlease try again in a moment.");
                    else
                        MCHelper.sendMessage("§cAn error occurred when attempting to block §9@" + user.getUsername() +
                                ". §cStatus code: " + r.getStatus() + " (" + r.getStatusText() + ")");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public static void unblock(String userId)
    {
        final User user = DiscordCE.client.getUserById(userId);

        // Checking if user exists
        if (user == null)
        {
            MCHelper.sendMessage("§cThe user you specified to unblock does not exist.");
            return;
        }

        // Checking if the user is already blocked
        if (!VolatileSettings.isBlocked(userId))
        {
            MCHelper.sendMessage("§cThe user you specified to block is not blocked.");
            return;
        }

        ConcurrentUtil.executor.execute(() -> {
            try
            {
                HttpResponse<JsonNode> r = Unirest.delete(Requester.DISCORD_API_PREFIX + "users/@me/relationships/{userId}")
                        .routeParam("userId", userId)
                        .header("authorization", DiscordCE.client.getAuthToken())
                        .header("user-agent", Requester.USER_AGENT)
                        .asJson();

                // Checking if request was ok
                if (r.getStatus() <= 199 || r.getStatus() >= 300)
                {
                    if (r.getStatus() == 429)
                        MCHelper.sendMessage("§cRate limit was reached when attempting to unblock §9@" + user.getUsername() +
                                ". §cPlease try again in a moment.");
                    else
                        MCHelper.sendMessage("§cAn error occurred when attempting to unblock §9@" + user.getUsername() +
                                ". §cStatus code: " + r.getStatus() + " (" + r.getStatusText() + ")");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
