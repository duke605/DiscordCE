package com.github.duke605.discordce.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Cole on 8/27/2016.
 */
public class CompressionUtil
{

    public static InputStream getInputstream(File compressedFile)
    {
        try
        {
            return new GZIPInputStream(new FileInputStream(compressedFile));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean saveToFile(File compressedFile, ByteArrayOutputStream boas)
    {
        try
        {
            // Creating file
            if (!compressedFile.exists())
            {
                compressedFile.getParentFile().mkdirs();
                compressedFile.createNewFile();
            }

            byte[] buffer = boas.toByteArray();
            IOUtils.closeQuietly(boas);
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(compressedFile));
            out.write(buffer, 0, buffer.length);
            IOUtils.closeQuietly(out);
            return true;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
