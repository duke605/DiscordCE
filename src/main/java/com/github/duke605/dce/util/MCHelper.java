package com.github.duke605.dce.util;

import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.VolatileSettings;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MCHelper {

    /**
     * Sends a message to the player
     *
     * @param message The message to send to the player
     */
    public static void sendMessage(String message)
    {
        if (Minecraft.getMinecraft().thePlayer == null)
            return;

        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
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

        IChatComponent component = IChatComponent.Serializer.func_150699_a(message.toString());
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
                .put("text", "\u00a7" + Config.mentionColour + "@" + name + "\u00a7r")
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
                .put("text", "\u00a7" + Config.serverColour + "$" + name + "\u00a7r")
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
                .put("text", "\u00a7" + Config.channelColour + name + "\u00a7r")
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
            return "\u00a7" + Config.friendColour + username + "\u00a7r";

        return "\u00a7" + Config.userColour + username + "\u00a7r";
    }

    /**
     * Colours a server's name
     *
     * @param name The name of the server
     * @return the name of the server wrapped in colour
     */
    public static String colourServer(String name)
    {
        return "\u00a7" + Config.serverColour + name + "\u00a7r";
    }

    /**
     * Colours a channel's name
     *
     * @param name The name of the channel
     * @return the name of the channel wrapped in colour
     */
    public static String colourChannel(String name)
    {
        return "\u00a7" + Config.channelColour + name + "\u00a7r";
    }

    /**
     * Builds a message that is clickable to send to the minecraft player
     *
     * @param name The username of the message sender
     * @param nick The nickname (if they have one) of the message sender
     * @param userId The id of the message sender
     * @param g The Guild the message was sent from (Null if DM)
     * @param c The Channel the message was sent from (Null if DM)
     * @param message The message as a string
     * @param template How to layout the message
     * @param m The message object
     * @return serialized json
     */
    public static String buildInteractiveMessage(String name
            , String nick
            , String userId
            , Guild g
            , Channel c
            , String message
            , String template
            , Message m)
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
                    .put("text", colourServer(DiscordUtil.overrideServerName(g.getName())))
                    .put("insertion", g.getId())
                    .put("hoverEvent", new JSONObject()
                        .put("action", "show_text")
                        .put("value", new JSONObject()
                            .put("text", "Shift + Click to append server's id.")))
                    .toString();

        if (c != null)
            channel = new JSONObject()
                    .put("text", colourChannel(DiscordUtil.overrideChannelName(c.getName())))
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
                    .replace("%u", "\"," + (nick == null ? username : nickname) + ",\"")
                    .replace("%m", StringEscapeUtils.escapeJson(message));
        else
            template = StringEscapeUtils.escapeJson(template)
                    .replace("%u", "\"," + username + ",\"")
                    .replace("%m", StringEscapeUtils.escapeJson(message));

        return  "[\"" + template + "\"]";
    }
}
