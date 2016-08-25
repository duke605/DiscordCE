package com.github.duke605.discordce.lib;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.HashMap;

public class Config {

    public static Configuration instance;

    // Credentials
    public static String email;
    public static String password;

    // Display
    public static String directLayout;
    public static String serverLayout;
    public static HashMap<String, String> serverOverrides = new HashMap<>();
    public static HashMap<String, String> channelOverrides = new HashMap<>();

    // Colours
    public static String friendColour;
    public static String mentionColour;
    public static String serverColour;
    public static String channelColour;
    public static String userColour;

    // Misc
    public static boolean deathMessages;
    public static boolean achievementMessages;

    public static final String CATEGORY_CREDENTIALS = "credentials";
    public static final String CATEGORY_DISPLAY = "display";
    public static final String CATEGORY_COLOUR = "colours";

    public static void load(File file) {
        if (instance == null)
            instance = new Configuration(file);

        instance.load();

        email = instance.getString("email"
                , CATEGORY_CREDENTIALS
                , ""
                , "Your DiscordCE email.");

        password = instance.getString("password"
                , CATEGORY_CREDENTIALS
                , ""
                , "Your DiscordCE password.");

        directLayout = instance.getString("directLayout"
                , CATEGORY_DISPLAY
                , "[DM] <%u>: %m"
                , "How direct messages from DiscordCE will be displayed.\r\n\t" +
                        "%u = The user the message originated from.\r\n\t" +
                        "%m = The message.\r\n" +
                        "You may use colours as long as the do not wrap variables " +
                        "http://minecraft.gamepedia.com/Formatting_codes");

        serverLayout = instance.getString("serverlayout"
                , CATEGORY_DISPLAY
                , "[%s][%c] <%U>: %m"
                , "How a message from a DiscordCE server/guild will be displayed.\r\n\t" +
                        "%s = The server the message originated from.\r\n\t" +
                        "%c = The channel the message originated from.\r\n\t" +
                        "%u = The user the message originated from. (This might make it harder to mention someone)\r\n\t" +
                        "%U = The user the message originated from. (Their username not their nick)\r\n\t" +
                        "%m = The message.\r\n" +
                        "You may use colours as long as the do not wrap variables " +
                        "http://minecraft.gamepedia.com/Formatting_codes");

        String[] sno = instance.getStringList("serverOverrides"
                , CATEGORY_DISPLAY
                , new String[0]
                , "If a server name is too long and takes up a lot of space when displayed you may override it by\r\n" +
                        "placing an override in the list below. For example if you have a server with the name\r\n" +
                        "\"Very long and annoying name\" and want to change it to \"VLAN\" when displayed put\r\n" +
                        "\"Very long and annoying name::VLAN\" in the list.");

        String[] cno = instance.getStringList("channelOverrides"
                , CATEGORY_DISPLAY
                , new String[0]
                , "If a channel name is too long and takes up a lot of space when displayed you may override it by\r\n" +
                        "placing an override in the list below. For example if you have a channel with the name\r\n" +
                        "\"Very long and annoying name\" and want to change it to \"VLAN\" when displayed put\r\n" +
                        "\"Very long and annoying name::VLAN\" in the list.");

        friendColour = instance.getString("friendColour"
                , CATEGORY_COLOUR
                , "5"
                , "When a friend sends a message their name will be displayed in a different colour\r\n" +
                        "so they are easily distinguished. Use http://minecraft.gamepedia.com/Formatting_codes\r\n" +
                        "to help find supported colours.");

        mentionColour = instance.getString("mentionColour"
                , CATEGORY_COLOUR
                , "9"
                , "When a channel, role, or user is mentioned the mention will be highlighted so you can clearly\r\n" +
                        "distinguish it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes\r\n" +
                        "to help find supported colours.");

        serverColour = instance.getString("serverColour"
                , CATEGORY_COLOUR
                , "2"
                , "When a message is displayed the server it came from can be coloured so you can clearly distinguish\r\n" +
                        "it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes to help\r\n" +
                        "find supported colours.");

        channelColour = instance.getString("channelColour"
                , CATEGORY_COLOUR
                , "a"
                , "When a message is displayed the channel it came from can be coloured so you can clearly distinguish\r\n" +
                        "it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes to help\r\n" +
                        "find supported colours.");

        userColour = instance.getString("userColour"
                , CATEGORY_COLOUR
                , "7"
                , "When a message is displayed the user it came from can be coloured so you can clearly distinguish\r\n" +
                        "it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes to help\r\n" +
                        "find supported colours.");

        deathMessages = instance.getBoolean("deathMessages"
                , Configuration.CATEGORY_GENERAL
                , false
                , "When you die a message will be sent to the channel you are currently talking to if this value is\r\n" +
                        "set to true.");

        achievementMessages = instance.getBoolean("achievementMessages"
                , Configuration.CATEGORY_GENERAL
                , false
                , "When an achievement is unlocked a message will be sent to the channel you are currently talking to\r\n" +
                        "if this value is set to true.");

        // Separating server overrides
        for(String o : sno) {
            String[] a = o.split("::");
            serverOverrides.put(a[0], a[1]);
        }

        // Separating channel overrides
        for(String o : cno) {
            String[] a = o.split("::");
            channelOverrides.put(a[0], a[1]);
        }

        instance.save();
    }

}
