package com.github.duke605.discordce.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentUtil
{
    public static ExecutorService executor = Executors.newFixedThreadPool(20, (r) -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });
}
