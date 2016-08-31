package com.github.duke605.dce.util;

import com.github.duke605.dce.handler.MinecraftEventHandler;
import com.github.duke605.dce.lib.VolatileSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentUtil
{
    public static ExecutorService executor = Executors.newFixedThreadPool(20, (r) -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    /**
     * Pushes a fetch image request to the queue so that when the image is done being fetched it
     * will be returned to the calling thread
     *
     * @param future The task being waited on
     * @param url The url of the image
     */
    public static void pushImageTaskToQueue(Future<BufferedImage> future, String url)
    {
        MinecraftEventHandler.queue.add(new AbstractMap.SimpleEntry<>(future, (image) ->
        {
            if (image == null)
            {
                VolatileSettings.icons.remove(url);
                return;
            }

            DynamicTexture t = new DynamicTexture((BufferedImage) image);
            Minecraft mc = Minecraft.getMinecraft();
            VolatileSettings.icons.put(url, mc.getTextureManager().getDynamicTextureLocation(url, t));
        }));
    }
}
