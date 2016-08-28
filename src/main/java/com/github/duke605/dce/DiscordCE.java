package com.github.duke605.dce;

import com.github.duke605.dce.command.*;
import com.github.duke605.dce.handler.DiscordEventHandler;
import com.github.duke605.dce.handler.MinecraftEventHandler;
import com.github.duke605.dce.lib.Config;
import com.github.duke605.dce.lib.Preferences;
import com.github.duke605.dce.lib.Reference;
import com.github.duke605.dce.util.ConcurrentUtil;
import net.dv8tion.jda.client.JDAClient;
import net.dv8tion.jda.client.JDAClientBuilder;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.io.File;

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
        // Connecting
        try
        {
            new JDAClientBuilder()
                    .addListener(new DiscordEventHandler())
                    .setEmail(Config.email)
                    .setPassword(Config.password)
                    .buildAsync();
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
    }
}
