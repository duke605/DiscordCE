package com.github.duke605.discordce.util;

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

    public static BufferedImage getImage(String url, Function<BufferedImage, BufferedImage> process)
    {
        File cacheDir = new File(Minecraft.getMinecraft().mcDataDir, "tmp");
        InputStream in = null;
        boolean flag = true;

        try
        {
            HttpResponse<String> r = Unirest.head(url).asString();

            // Checking if request was ok
            if (!isOk(r.getStatus()))
                return null;

            // Getting image md5 from header
            String md5 = r.getHeaders().getFirst("etag").substring(1, 32);
            File cacheFile = new File(cacheDir, md5 + ".jpg.gz");

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
                if (!isOk(r.getStatus()))
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

    public static boolean isOk(int code)
    {
        return code > 199 && code < 300;
    }
}
