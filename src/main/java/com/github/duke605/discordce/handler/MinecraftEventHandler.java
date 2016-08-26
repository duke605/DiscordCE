package com.github.duke605.discordce.handler;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.gui.GuiDiscordCeMenu;
import com.github.duke605.discordce.lib.Config;
import com.github.duke605.discordce.lib.Preferences;
import com.github.duke605.discordce.lib.Reference;
import com.github.duke605.discordce.util.ConcurrentUtil;
import com.github.duke605.discordce.util.MCHelper;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.json.JSONObject;

import java.io.File;

public class MinecraftEventHandler
{

    private long lastTyping = 0;

     @SubscribeEvent
    public void onPlayerJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent e)
    {
        if (e.isLocal())
            ConcurrentUtil.executor.execute(() ->
                    DiscordCE.client.getAccountManager().setGame("Minecraft [SP]"));
        else
            ConcurrentUtil.executor.execute(() ->
                DiscordCE.client.getAccountManager().setGame("Minecraft [MP]"));
    }

    @SubscribeEvent
    public void onPlayerLeaveServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
    {
        ConcurrentUtil.executor.execute(() ->
                DiscordCE.client.getAccountManager().setGame("Minecraft [Menus]"));
    }

    @SubscribeEvent
    public void sendTyping(TickEvent e)
    {
        if (e.type != TickEvent.Type.CLIENT
                || DiscordCE.client == null
                || DiscordCE.client.getTextChannelById(Preferences.i.usingChannel) == null
                || lastTyping > System.currentTimeMillis() - 9000
                || Minecraft.getMinecraft().thePlayer == null
                || Minecraft.getMinecraft().currentScreen == null
                || !(Minecraft.getMinecraft().currentScreen instanceof GuiChat))
            return;

        GuiChat gui = (GuiChat) Minecraft.getMinecraft().currentScreen;
        GuiTextField field = ReflectionHelper.getPrivateValue(GuiChat.class, gui, 4);

        // Players is typing in command and not sending message
        if (field.getText().isEmpty() || !field.getText().startsWith("//"))
            return;

        field.setMaxStringLength(2000);

        // Setting the last time the typing packet was sent
        lastTyping = System.currentTimeMillis();

        // Sending typing packet in new thread so it doesn't lag
        ConcurrentUtil.executor.execute(() ->
                DiscordCE.client.getTextChannelById(Preferences.i.usingChannel).sendTyping());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (DiscordCE.test.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new GuiDiscordCeMenu());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerDeathMessage(LivingDeathEvent e)
    {
        if (!Config.deathMessages
                || !(e.getEntityLiving() instanceof EntityPlayer)
                || e.getEntityLiving().getUniqueID() != Minecraft.getMinecraft().thePlayer.getUniqueID())
            return;

        // Getting discord and minecraft user
        EntityPlayer player = (EntityPlayer) e.getEntityLiving();
        User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());

        // Getting death message
        String deathMessage = e.getSource().getDeathMessage(e.getEntityLiving()).getUnformattedText();

        // Replacing minecraft name with discord name
        deathMessage = deathMessage.replaceAll(player.getDisplayNameString(), me.getAsMention());

        // Sending death message
        DiscordCE.client.getTextChannelById(Preferences.i.usingChannel).sendMessageAsync(deathMessage, null);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerDeathImage(GuiOpenEvent e)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (!(e.getGui() instanceof GuiGameOver)
                || !Config.demiseImage
                || player == null
                || player.getHealth() <= 0F)
            return;

        // Getting discord and minecraft user
        try
        {
            User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());
            Minecraft mc = Minecraft.getMinecraft();
            ITextComponent t = ScreenShotHelper.saveScreenshot(mc.mcDataDir,
                    mc.displayWidth,
                    mc.displayHeight,
                    mc.getFramebuffer());
            String fileName = new JSONObject(TextComponentBase.Serializer.componentToJson(t)).getJSONArray("with")
                    .getJSONObject(0).getJSONObject("clickEvent").getString("value");
            File file = new File(fileName);
            TextChannel c = DiscordCE.client.getTextChannelById(Preferences.i.usingChannel);

            // Doing checks
            if (c == null || !c.checkPermission(me, Permission.MESSAGE_ATTACH_FILES))
                return;

            //Sending file
            c.sendFileAsync(file, null, m -> file.delete());
        }

        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onAchievement(AchievementEvent e)
    {
        if (!Config.achievementMessages
                || !(e.getEntityLiving() instanceof EntityPlayer)
                || e.getEntityLiving().getUniqueID() != Minecraft.getMinecraft().thePlayer.getUniqueID())
            return;

        // Getting discord and minecraft user
        EntityPlayer player = (EntityPlayer) e.getEntityLiving();
        User me = DiscordCE.client.getUserById(DiscordCE.client.getSelfInfo().getId());

        // Getting unlocked ac
        String aString = me.getAsMention() +
                " has just earned the achievement \"" +
                e.getAchievement().getStatName().getUnformattedComponentText() + ".\"";

        // Sending death message
        DiscordCE.client.getTextChannelById(Preferences.i.usingChannel).sendMessageAsync(aString, null);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPostConfigChange(ConfigChangedEvent.PostConfigChangedEvent e)
    {
        if (!e.getModID().equals(Reference.MODID))
            return;

        // Loading the configurations again
        Config.instance.save();
        Config.load(null);

        // Telling player configs were changed
        MCHelper.sendMessage(TextFormatting.GRAY + "Configurations updated.");
    }
}
