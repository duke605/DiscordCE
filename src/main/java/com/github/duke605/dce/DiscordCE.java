package com.github.duke605.dce;

import com.github.duke605.dce.command.*;
import com.github.duke605.dce.handler.DiscordEventHandler;
import com.github.duke605.dce.handler.MinecraftEventHandler;
import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.Preferences;
import com.github.duke605.dce.lib.Reference;
import com.github.duke605.dce.util.ConcurrentUtil;
import com.github.duke605.dce.util.HttpUtil;
import com.github.duke605.dce.util.MixpanelUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import net.dv8tion.jda.client.JDAClient;
import net.dv8tion.jda.client.JDAClientBuilder;
import net.dv8tion.jda.requests.Requester;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.json.JSONObject;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Mod(
        modid = Reference.MODID,
        name = Reference.NAME,
        version = Reference.VERSION,
        clientSideOnly = true,
        guiFactory = "com.github.duke605.dce.gui.factory.GuiFactoryDiscordConfig"
)
public class DiscordCE
{

    @Mod.Instance(Reference.MODID)
    public static DiscordCE instance;

    // Mod start time
    public static long startTime = System.currentTimeMillis();

    // Will be set when ready event called
    public static volatile JDAClient client;

    // Keybindings
    public static KeyBinding test;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        Config.load(e.getSuggestedConfigurationFile());
        Preferences.load(new File(e.getModConfigurationDirectory(), "dce.json.tar.gz"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        String token = null;

        // Checking if we have their token
        try
        {
            File tokenFile = new File("tokens.json");
            String tempToken;

            // Checking if the file exists
            if (!tokenFile.exists())
                throw new FileNotFoundException();

            // Reading from the file
            try (JsonReader in = new JsonReader(new InputStreamReader(new FileInputStream(tokenFile))))
            {
                // Getting the tokens object
                JsonObject tokens = new JsonParser().parse(in).getAsJsonObject();

                // Checking if it contains the current players email
                if (tokens.has(Config.email))
                    tempToken = tokens.get(Config.email).getAsString();
                else
                    throw new FileNotFoundException();

                // Checking if token is valid
                HttpResponse r = Unirest.get(Requester.DISCORD_API_PREFIX+"users/@me/guilds")
                        .header("authorization", tempToken)
                        .header("user-agent", Requester.USER_AGENT)
                        .asString();

                // Checking if good
                if (HttpUtil.isOk(r.getStatus()))
                    token = tempToken;
            }
        }
        catch (FileNotFoundException ex) {}
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // Connecting
        try
        {
            // Building client
            JDAClientBuilder b = new JDAClientBuilder()
                    .addListener(new DiscordEventHandler())
                    .setAudioEnabled(false);

            // Checking if MFA code is needed
            if (token == null)
            {
                // Setting email
                b.setEmail(Config.email.trim().isEmpty()
                    ? JOptionPane.showInputDialog(null, "Please enter your email address.")
                    : Config.email);

                // Getting user password
                b.setPassword(JOptionPane.showInputDialog(null, "Please enter your password for " + Config.email));

                // Getting MFA Code
                b.setCode(JOptionPane.showInputDialog(null, "Please enter the token from your two-factor " +
                        "authentication mobile app.\nIf you do not use two-factor authentication please press \"OK.\""));
            }

            // Token already gotten no need to get MFA code or password
            else
                b.setClientToken(token);

            b.buildAsync();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            ConcurrentUtil.executor.execute(() ->
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        ClientCommandHandler.instance.registerCommand(new CommandChat());
        ClientCommandHandler.instance.registerCommand(new CommandSwitchChannel());
        ClientCommandHandler.instance.registerCommand(new CommandBlock());
        ClientCommandHandler.instance.registerCommand(new CommandUnblock());
        ClientCommandHandler.instance.registerCommand(new CommandAddFriend());
        ClientCommandHandler.instance.registerCommand(new CommandDeleteFriend());
        ClientCommandHandler.instance.registerCommand(new CommandFocus());
        ClientCommandHandler.instance.registerCommand(new CommandDM());

        ClientRegistry.registerKeyBinding(test = new KeyBinding("Open DiscordCE menu", Keyboard.KEY_Y, "DiscordCE"));

        MinecraftForge.EVENT_BUS.register(new MinecraftEventHandler());

        // Tracking sign on
        if (Config.trackSignOn)
            ConcurrentUtil.executor.execute(() -> {
                TimeZone tz = Calendar.getInstance().getTimeZone();
                Date now = Calendar.getInstance().getTime();
                String time = new SimpleDateFormat("EEEE, MMMM d, YYYY h:mma").format(now);

                JSONObject props = new JSONObject()
                        .put("forge_version", ForgeVersion.getVersion())
                        .put("mc_version", MinecraftForge.MC_VERSION)
                        .put("timezone", tz.getDisplayName(Locale.CANADA))
                        .put("local_timestamp", new Date().getTime())
                        .put("local_time", time);

                MixpanelUtil.sendEvent("Start Game", props);
            });
    }
}
