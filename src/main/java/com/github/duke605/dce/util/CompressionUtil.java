package com.github.duke605.dce.util;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil
{

    /**
     * Gets the compressed input stream for an image
     *
     * @param compressedFile The compressed file to get the {@link InputStream} for
     * @return the {@link InputStream} for the passed file
     */
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

    /**
     * Saves the passed bytes to the passed file
     *
     * @param compressedFile The file the bytes are to be saved to
     * @param boas The {@link ByteArrayOutputStream} containing the bytes to be saved
     * @return true if the file was successfully saved
     */
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
