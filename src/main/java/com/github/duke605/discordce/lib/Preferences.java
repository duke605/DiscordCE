package com.github.duke605.discordce.lib;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

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

        try (TarArchiveInputStream in = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(f))))
        {
            in.getNextTarEntry();
            try (JsonReader reader = new JsonReader(new InputStreamReader(in)))
            {
                i = new Gson().fromJson(reader, Preferences.class);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void save()
    {
        try (TarArchiveOutputStream out = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file))))
        {
            TarArchiveEntry e = new TarArchiveEntry("dce.json");
            String json = new Gson().toJson(i);
            e.setSize(json.getBytes().length);
            out.putArchiveEntry(e);
            out.write(json.getBytes(), 0, json.getBytes().length);
            out.closeArchiveEntry();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void load(File f)
//    {
//        file = f;
//
//        // Creating file if it does not exist
//        if (!f.exists())
//            save();
//
//        try (JsonReader in = new JsonReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)))))
//        {
//            i = new Gson().fromJson(in, Preferences.class);
//        }
//
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public static void save()
//    {
//        try (PrintWriter out = new PrintWriter(new GZIPOutputStream(new FileOutputStream(file))))
//        {
//            out.println(new Gson().toJson(i));
//        }
//
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
    //</editor-fold>
}
