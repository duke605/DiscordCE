package com.github.duke605.discordce.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil
{

    /**
     * Replaces all occurrences of the pattern in this input
     *
     * @param p The pattern to match on
     * @param s The input string
     * @param r The template of the replacement string
     * @param cb The callback that returns an array of objects to place into the
     *           template
     * @return a string will all matched patterns replaced
     */
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
