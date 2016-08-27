package com.github.duke605.discordce.gui;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.gui.abstraction.GuiEmbeddedList;
import com.github.duke605.discordce.gui.abstraction.GuiListContainer;
import com.github.duke605.discordce.util.DrawingUtils;
import com.github.duke605.discordce.util.HttpUtil;
import com.google.common.collect.ImmutableList;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.GuildImpl;
import net.dv8tion.jda.requests.Requester;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GuiUsers extends GuiListContainer
{
    private GuiUserList userList;
    private GuiScreen parent;
    private GuiButton next;
    private GuiButton prev;
    public List<User> users;
    public List<Guild> guilds;
    public GuiTextField search;
    static HashMap<String, ResourceLocation> guildIcons = new HashMap<>();

    public GuiUsers(GuiScreen parent)
    {
        this.parent = parent;
        this.users = DiscordCE.client.getUsers();
        this.guilds = DiscordCE.client.getGuilds();

        DiscordCE.client.getGuilds().forEach(g -> {
            BufferedImage image = null;
            try
            {
                if (guildIcons.containsKey(g.getId()))
                    return;

                image = HttpUtil.getImage(g.getIconUrl(), DrawingUtils::circularize);

                if (image == null)
                    throw new FileNotFoundException("guild icon could not be found");

                DynamicTexture t = new DynamicTexture(image);
                Minecraft mc = Minecraft.getMinecraft();
                guildIcons.put(g.getId(), mc.getTextureManager().getDynamicTextureLocation(g.getId(), t));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initGui()
    {
        search = new GuiTextField(0, mc.fontRendererObj, 11, height - 46, 200, 19);
        userList = new GuiUserList(mc, this);
        buttonList.add(new GuiButton(0
                , 10
                , ((fontRendererObj.FONT_HEIGHT + 16)/2) - 9
                , 40
                , 20
                , "< Back"));

        buttonList.add(prev = new GuiButton(-1
                , 10
                , height - 22
                , 40
                , 20
                , "< Prev"));

        buttonList.add(next = new GuiButton(-2
                , width - 50
                , height - 22
                , 40
                , 20
                , "Next >"));
        prev.enabled = false;
    }

    @Override
    protected void actionPerformed(GuiButton b) throws IOException
    {
        // Previous page
        if (b.id == -1)
        {
            next.enabled = true;

            if (--userList.page == 0)
                b.enabled = false;

            userList.initList();
        }

        // Next page
        else if (b.id == -2)
        {
            prev.enabled = true;
            userList.page++;

            if (Math.ceil(users.size() / 30) - 1 == userList.page)
                b.enabled = false;

            userList.initList();
        }

        // Back to parent
        else if (b.id == 0)
            mc.displayGuiScreen(parent);
    }

    @Override
    protected void mouseClicked(int x, int y, int u) throws IOException
    {
        super.mouseClicked(x, y, u);
        search.mouseClicked(x, y, u);
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        this.drawDefaultBackground();
        userList.drawScreen(x, y, f);
        search.drawTextBox();
        super.drawScreen(x, y, f);
    }

    @Override
    public GuiEmbeddedList getEmbeddedList()
    {
        return userList;
    }

    @Override
    protected void keyTyped(char c, int code) throws IOException
    {
        if(code == 1)
            mc.displayGuiScreen(parent);

        else if (search.isFocused())
        {
            search.textboxKeyTyped(c, code);
            userList.page = 0;
            userList.initList();

            // Enabling buttons
            if (search.getText().isEmpty())
            {
                next.enabled = true;
                prev.enabled = true;
            }

            // Disabling buttons
            else
            {
                next.enabled = false;
                prev.enabled = false;
            }
        }
    }

    @Override
    public void updateScreen()
    {
        search.updateCursorCounter();
    }
}
