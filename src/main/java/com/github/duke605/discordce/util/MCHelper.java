package com.github.duke605.discordce.util;

import com.github.duke605.discordce.lib.Config;
import com.github.duke605.discordce.lib.VolatileSettings;
import com.sun.istack.internal.NotNull;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Cole on 8/20/2016.
 */
public class MCHelper {

    /**
     * Sends a message to the player
     *
     * @param message The message to send to the player
     */
    public static void sendMessage(String message)
    {
        if (Minecraft.getMinecraft() == null
                || Minecraft.getMinecraft().thePlayer == null)
            return;

        Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString(message));
    }

    /**
     * Sends an interactive message to the user
     *
     * @param message The message to set the user
     */
    public static void sendRawMessage(JSONArray message)
    {
        if (Minecraft.getMinecraft() == null
                || Minecraft.getMinecraft().thePlayer == null)
            return;

        ITextComponent component = TextComponentBase.Serializer.jsonToComponent(message.toString());
        Minecraft.getMinecraft().thePlayer.addChatMessage(component);
    }

    /**
     * Creates an interactive mention
     *
     * @param userId The id of the user that was mentioned
     * @param name The name of the user that was mentions
     * @return a mention as a json object
     */
    public static JSONObject mentionAsRaw(String userId, String name)
    {
        return new JSONObject()
                .put("text", "§" + Config.mentionColour + "@" + name + "§r")
                .put("insertion", userId)
                .put("hoverEvent", new JSONObject()
                        .put("action", "show_text")
                        .put("value", "Shift + Click to append users's id."));
    }

    /**
     * Creates an interactive server mention
     *
     * @param serverId The id of the server that was mentioned
     * @param name The name of the server that was mentions
     * @return a mention as a json object
     */
    public static JSONObject serverAsRaw(String serverId, String name)
    {
        return new JSONObject()
                .put("text", "§" + Config.serverColour + "$" + name + "§r")
                .put("insertion", serverId)
                .put("hoverEvent", new JSONObject()
                        .put("action", "show_text")
                        .put("value", "Shift + Click to append server's id."));
    }

    /**
     * Creates an interactive channel mention
     *
     * @param channelId The id of the channel that was mentioned
     * @param name The name of the channel that was mentions
     * @return a mention as a json object
     */
    public static JSONObject channelAsRaw(String channelId, String name)
    {
        return new JSONObject()
                .put("text", "§" + Config.channelColour + name + "§r")
                .put("insertion", channelId)
                .put("clickEvent", new JSONObject()
                    .put("action", "run_command")
                    .put("value", "/switchChannel " + channelId))
                .put("hoverEvent", new JSONObject()
                        .put("action", "show_text")
                        .put("value", "Shift + Click to append channel's id."));
    }

    /**
     * Colours a users name if they are a friend
     *
     * @param userId The id of the user
     * @param username The user's name
     * @return the name of the user wrapped in colour
     */
    public static String colourUser(String userId, String username)
    {
        if (VolatileSettings.isFriend(userId))
            return "§" + Config.friendColour + username + "§r";

        return "§" + Config.userColour + username + "§r";
    }

    /**
     * Colours a server's name
     *
     * @param name The name of the server
     * @return the name of the server wrapped in colour
     */
    public static String colourServer(String name)
    {
        return "§" + Config.serverColour + name + "§r";
    }

    /**
     * Colours a channel's name
     *
     * @param name The name of the channel
     * @return the name of the channel wrapped in colour
     */
    public static String colourChannel(String name)
    {
        return "§" + Config.channelColour + name + "§r";
    }

    public static String buildInteractiveMessage(@NotNull String name
            , String nick
            , @NotNull String userId
            , Guild g
            , Channel c
            , @NotNull String message
            , @NotNull String template
            , @NotNull Message m)
    {
        String server = "";
        String nickname = "";
        String channel = "";
        String username;

        JSONObject id = new JSONObject()
                .put("text", "")
                .put("id", m.getId());

        if (g != null)
            server = new JSONObject()
                    .put("text", colourServer(g.getName()))
                    .put("insertion", g.getId())
                    .put("hoverEvent", new JSONObject()
                        .put("action", "show_text")
                        .put("value", new JSONObject()
                            .put("text", "Shift + Click to append server's id.")))
                    .toString();

        if (c != null)
            channel = new JSONObject()
                    .put("text", colourChannel(c.getName()))
                    .put("insertion", c.getId())
                    .put("clickEvent", new JSONObject()
                        .put("action", "run_command")
                        .put("value", "/switchChannel " + c.getId()))
                    .put("hoverEvent", new JSONObject()
                            .put("action", "show_text")
                            .put("value", "Click to switch to channel\n" +
                                          "Shift + Click to append channel's id."))
                    .toString();

        username = new JSONObject()
                .put("text", colourUser(userId, name))
                .put("insertion", userId)
                .put("hoverEvent", new JSONObject()
                    .put("action", "show_text")
                    .put("value", "Shift + Click to append user's id."))
                .toString();

        if (nick != null)
            nickname = new JSONObject()
                    .put("text", colourUser(userId, nick))
                    .put("insertion", userId)
                    .put("hoverEvent", new JSONObject()
                            .put("action", "show_text")
                            .put("value", "Shift + Click to append user's id."))
                    .toString();

        if (g != null)
            template = StringEscapeUtils.escapeJson(template)
                    .replace("%c", "\"," + channel + ",\"")
                    .replace("%s", "\"," + server + ",\"")
                    .replace("%U", "\"," + username + ",\"")
                    .replace("%u", "\"," + nickname + ",\"")
                    .replace("%m", StringEscapeUtils.escapeJson(message));
        else
            template = StringEscapeUtils.escapeJson(template)
                    .replace("%u", "\"," + username + ",\"")
                    .replace("%m", StringEscapeUtils.escapeJson(message));

        return  "[\"" + template + "\"]";
    }
}
