package com.github.duke605.discordce.util;

/**
 * Created by Cole on 8/21/2016.
 */
public class NumberUtil
{
    public static boolean isInt(String num)
    {
        try
        {
            Integer.parseInt(num);
            return true;
        } catch (Exception e) {}

        return false;
    }

    public static boolean isDouble(String num)
    {
        try
        {
            Double.parseDouble(num);
            return true;
        } catch (Exception e) {}

        return false;
    }
}
