package com.github.duke605.dce.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.function.Function;

public class HttpUtil
{

    /**
     * Fetches an image from the cache or the remote if it's not in the cache
     *
     * @param url The url of the image to fetch
     * @param process A callback that allows processing to be done to the image before it is
     *                returned. null if no processing is to be done.
     * @return the image or null if an error occurred and the image could not be fetched
     */
    public static BufferedImage getImage(String url, Function<BufferedImage, BufferedImage> process)
    {
        File cacheDir = new File(Minecraft.getMinecraft().mcDataDir, "tmp");
        InputStream in = null;
        boolean flag = true;

        try
        {
            File cacheFile = new File(cacheDir, url.replaceAll("https?://", "") + ".gz");

            // Checking if in cache
            if (cacheFile.exists())
            {
                in = CompressionUtil.getInputstream(cacheFile);

                // Setting flag to false so it does not save file
                if (in != null)
                    flag = false;
            }

            // Getting remote inputstream
            if (in == null) {
                HttpResponse<InputStream> r1 = Unirest.get(url).asBinary();

                // Checking if response was ok
                if (!isOk(r1.getStatus()))
                    return null;

                in = r1.getBody();
            }

            // Getting image from inputstream
            BufferedImage image = ImageIO.read(in);

            // Closing input
            in.close();

            if (process != null)
                image = process.apply(image);

            if (!flag)
                return image;

            // Saving to cache
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            CompressionUtil.saveToFile(cacheFile, baos);

            return image;
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Checks if the status code is acceptable
     *
     * @param code The status code of the response
     * @return true if the code is ok
     */
    public static boolean isOk(int code)
    {
        return code > 199 && code < 300;
    }
}
