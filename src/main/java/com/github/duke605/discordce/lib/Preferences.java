package com.github.duke605.discordce.lib;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Preferences
{
    public static Preferences i = new Preferences();
    public static File file;

    //<editor-fold desc="Fields">
    public String usingChannel = "";
    public String usingGuild = "";

    public List<String> mutedGuilds = new ArrayList<>();
    public List<String> mutedChannels = new ArrayList<>();

    public boolean focus = false;
    //</editor-fold>

    //<editor-fold desc="IO Methods">
    public static void load(File f)
    {
        file = f;

        // Creating file if it does not exist
        if (!f.exists())
            save();

        try (JsonReader in = new JsonReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)))))
        {
            i = new Gson().fromJson(in, Preferences.class);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void save()
    {
        try (PrintWriter out = new PrintWriter(new GZIPOutputStream(new FileOutputStream(file))))
        {
            out.println(new Gson().toJson(i));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //</editor-fold>
}
