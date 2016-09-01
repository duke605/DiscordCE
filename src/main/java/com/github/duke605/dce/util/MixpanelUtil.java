package com.github.duke605.dce.util;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.handler.MinecraftEventHandler;
import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.Reference;
import com.github.duke605.dce.lib.Secret;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MixpanelUtil
{
    public static final boolean debugging = false;

    /**
     * @return A constructed message builder object initialized with token
     */
    public static MessageBuilder getMessageBuilder()
    {
        return new MessageBuilder(Secret.MIXPANEL_TOKEN);
    }

    /**
     * Sends a track event with game startup information
     *
     * @param runAsync Whether or not to run async
     */
    public static void setStartGameEvent(boolean runAsync)
    {
        // Stopping if user does not want to send this kind of event
        if (!Config.trackGameStart)
            return;

        Runnable r = () ->
        {
            TimeZone tz = Calendar.getInstance().getTimeZone();
            Date now = Calendar.getInstance().getTime();
            String time = new SimpleDateFormat("EEEE, MMMM d, YYYY h:mma").format(now);

            JSONObject props = new JSONObject()
                    .put("forge_version", ForgeVersion.getVersion())
                    .put("mc_version", MinecraftForge.MC_VERSION)
                    .put("language", FMLCommonHandler.instance().getCurrentLanguage())
                    .put("dce_version", Reference.VERSION);

            MixpanelUtil.sendEvent("Start Game", props);
        };

        // Running async
        if (runAsync)
            ConcurrentUtil.executor.execute(r);

            // Run sync
        else
            r.run();
    }

    /**
     * Sends a track event when the game is stopped
     *
     * @param runAsync Whether or not to run async
     */
    public static void sentGameStopEvent(boolean runAsync)
    {
        // Stopping if user does not want to send this kind of event
        if (!Config.trackGameStop)
            return;

        Runnable r = () -> {
            long gameTime = System.currentTimeMillis() - DiscordCE.startTime;

            JSONObject props = new JSONObject()
                    .put("single_player_games", MinecraftEventHandler.singlePlayerGames)
                    .put("play_time", gameTime)
                    .put("multiplayer_games", MinecraftEventHandler.multiplayerGames)
                    .put("num_guilds", DiscordCE.client.getGuilds().size())
                    .put("num_users", DiscordCE.client.getUsers().size());

            MixpanelUtil.sendEvent("Stop Game", props);
        };

        // Running async
        if (runAsync)
            ConcurrentUtil.executor.execute(r);

            // Run sync
        else
            r.run();
    }

    public static boolean sendEvent(String eventName, JSONObject props)
    {
        // Checking if email is blank
        if (Config.email.trim().isEmpty() || debugging)
            return false;

        MessageBuilder b = getMessageBuilder();
        JSONObject event = b.event(Config.emailHash, eventName, props);

        try
        {
            new MixpanelAPI().sendMessage(event);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}