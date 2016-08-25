package com.github.duke605.discordce.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cole on 8/24/2016.
 */
public class RegexUtil
{
    public static String replaceAll(Pattern p, String s, String r, Function<Matcher, Object[]> cb)
    {
        Matcher m = p.matcher(s);
        while(m.find())
        {
            s = m.replaceFirst(String.format(r, cb.apply(m)));
            m = p.matcher(s);
        }

        return s;
    }
}
