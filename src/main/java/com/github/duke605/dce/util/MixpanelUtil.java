package com.github.duke605.dce.util;

import com.github.duke605.dce.lib.Secret;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.json.JSONObject;

public class MixpanelUtil
{

    public static MessageBuilder getMessageBuilder()
    {
        return new MessageBuilder(Secret.MIXPANEL_TOKEN);
    }

    public static boolean sendEvent(String eventName, String id, JSONObject props)
    {
        MessageBuilder b = getMessageBuilder();
        JSONObject event = b.event(id, eventName, props);

        try
        {
            new MixpanelAPI().sendMessage(event);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
