package com.github.duke605.discordce.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Arrays {

    public static <T> String join(T[] array, String glue) {
        return join(java.util.Arrays.asList(array), glue);
    }

    public static <T> String join(Iterable<T> array, String glue) {
        final StringBuilder ret = new StringBuilder("");

        stream(array)
                .filter(e -> e != null && !e.toString().trim().isEmpty())
                .forEach(k -> ret.append(k.toString()).append(glue));

        if (ret.length() >= glue.length())
            ret.delete(ret.toString().length() - glue.length(), ret.toString().length());

        return ret.toString();
    }

    public static <T> Stream<T> stream(Iterable<T> array) {
        ArrayList<T> list = new ArrayList<>();

        for(T e : array)
            list.add(e);

        return list.stream();
    }

    public static String truncate(String s, int max) {
        return s.substring(0, Math.min(s.length(), max-3)) + (Math.min(s.length(), max-3) == s.length()
                ? ""
                : "...");
    }
}
