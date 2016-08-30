package com.github.duke605.dce.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;

public class Config {

    public static Configuration instance;

    // Credentials
    public static String emailHash;
    public static String email;
    public static Property emailProp;

    // Display
    public static String directLayout;
    public static String serverLayout;
    public static HashMap<String, String> serverOverrides = new HashMap<>();
    public static HashMap<String, String> channelOverrides = new HashMap<>();
    public static boolean userAvatars;
    public static boolean guildIcons;

    // Colours
    public static String friendColour;
    public static String mentionColour;
    public static String serverColour;
    public static String channelColour;
    public static String userColour;

    // Misc
    public static boolean deathMessages;
    public static boolean achievementMessages;
    public static boolean demiseImage;

    // Tracking
    public static boolean trackSignOn;

    public static final String CATEGORY_CREDENTIALS = "credentials";
    public static final String CATEGORY_DISPLAY = "display";
    public static final String CATEGORY_COLOUR = "colours";
    public static final String CATEGORY_PRIVACY = "privacy";

    public static void load(File file) {
        if (instance == null)
            instance = new Configuration(file);

        instance.load();

        emailProp = instance.get(CATEGORY_CREDENTIALS
                , "email"
                , ""
                , "Your DiscordCE email.");

        email = emailProp.getString();

        directLayout = instance.getString("directLayout"
                , CATEGORY_DISPLAY
                , "[DM] <%u>: %m"
                , "How direct messages from DiscordCE will be displayed.\n\t" +
                        "%u = The user the message originated from.\n\t" +
                        "%m = The message.\n" +
                        "You may use colours as long as the do not wrap variables " +
                        "http://minecraft.gamepedia.com/Formatting_codes");

        serverLayout = instance.getString("serverlayout"
                , CATEGORY_DISPLAY
                , "[%s][%c] <%U>: %m"
                , "How a message from a DiscordCE server/guild will be displayed.\n\t" +
                        "%s = The server the message originated from.\n\t" +
                        "%c = The channel the message originated from.\n\t" +
                        "%u = The user the message originated from. (This might make it harder to mention someone)\n\t" +
                        "%U = The user the message originated from. (Their username not their nick)\n\t" +
                        "%m = The message.\n" +
                        "You may use colours as long as the do not wrap variables " +
                        "http://minecraft.gamepedia.com/Formatting_codes");

        String[] sno = instance.getStringList("serverOverrides"
                , CATEGORY_DISPLAY
                , new String[0]
                , "If a server name is too long and takes up a lot of space when displayed you may override it by\n" +
                        "placing an override in the list below. For example if you have a server with the name\n" +
                        "\"Very long and annoying name\" and want to change it to \"VLAN\" when displayed put\n" +
                        "\"Very long and annoying name::VLAN\" in the list.");

        String[] cno = instance.getStringList("channelOverrides"
                , CATEGORY_DISPLAY
                , new String[0]
                , "If a channel name is too long and takes up a lot of space when displayed you may override it by\n" +
                        "placing an override in the list below. For example if you have a channel with the name\n" +
                        "\"Very long and annoying name\" and want to change it to \"VLAN\" when displayed put\n" +
                        "\"Very long and annoying name::VLAN\" in the list.");

        userAvatars = instance.getBoolean("userAvatars"
                , CATEGORY_DISPLAY
                , false
                , "Determines if user avatars will be displayed beside usernames in the GUI. !!WANING!! setting this\n" +
                        "option to true will download all user avatars potentially using up A LOT of storage. Using\n" +
                        "this feature when connected to multiple servers that have thousands of users is not advised.");

        guildIcons = instance.getBoolean("guildIcons"
                , CATEGORY_DISPLAY
                , true
                , "Determines if guild icons will be displayed in the GUI. !!WANING!! setting this option to true\n" +
                        "will download all guild icons potentially using up A LOT of storage. Using the feature\n" +
                        "when connected to 500 or more guilds is not advised.");

        friendColour = instance.getString("friendColour"
                , CATEGORY_COLOUR
                , "5"
                , "When a friend sends a message their name will be displayed in a different colour\n" +
                        "so they are easily distinguished. Use http://minecraft.gamepedia.com/Formatting_codes\n" +
                        "to help find supported colours.");

        mentionColour = instance.getString("mentionColour"
                , CATEGORY_COLOUR
                , "9"
                , "When a channel, role, or user is mentioned the mention will be highlighted so you can clearly\n" +
                        "distinguish it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes\n" +
                        "to help find supported colours.");

        serverColour = instance.getString("serverColour"
                , CATEGORY_COLOUR
                , "2"
                , "When a message is displayed the server it came from can be coloured so you can clearly distinguish\n" +
                        "it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes to help\n" +
                        "find supported colours.");

        channelColour = instance.getString("channelColour"
                , CATEGORY_COLOUR
                , "a"
                , "When a message is displayed the channel it came from can be coloured so you can clearly distinguish\n" +
                        "it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes to help\n" +
                        "find supported colours.");

        userColour = instance.getString("userColour"
                , CATEGORY_COLOUR
                , "7"
                , "When a message is displayed the user it came from can be coloured so you can clearly distinguish\n" +
                        "it from the rest of the message. Use http://minecraft.gamepedia.com/Formatting_codes to help\n" +
                        "find supported colours.");

        deathMessages = instance.getBoolean("deathMessages"
                , Configuration.CATEGORY_GENERAL
                , false
                , "When you die a message will be sent to the channel you are currently talking to if this value is\n" +
                        "set to true.");

        achievementMessages = instance.getBoolean("achievementMessages"
                , Configuration.CATEGORY_GENERAL
                , false
                , "When an achievement is unlocked a message will be sent to the channel you are currently talking to\n" +
                        "if this value is set to true.");

        demiseImage = instance.getBoolean("demiseImage"
                , Configuration.CATEGORY_GENERAL
                , false
                , "When you die an screenshot will be taken and sent to the channel you are currently talking to if\n" +
                        "value is set to true.");

        trackSignOn = instance.getBoolean("trackSignOn"
                , CATEGORY_PRIVACY
                , true
                , "Tracks when you start up a game of Minecarft. All data is submitted using your SHA1 hashed email so\n" +
                        "for all intents and purposes you remain anonymous and you email remains safe.");

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

        emailHash = hash(email);
    }

    public static String hash(String s)
    {
        // Hashing String
        try
        {
            return Hex.encodeHexString(MessageDigest.getInstance("SHA1").digest(s.getBytes()));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
