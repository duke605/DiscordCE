package com.github.duke605.dce.event;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.contract.CustomListenerAdapter;
import org.json.JSONObject;

public class RelationshipRemoveEvent
{
    public String id;
    public int type;

    public RelationshipRemoveEvent(JSONObject o) {
        id = o.getString("id");
        type = o.getInt("type");
    }

    public void post()
    {
        DiscordCE.client
                .getRegisteredListeners()
                .stream()
                .filter(o -> o instanceof CustomListenerAdapter)
                .forEach(o -> ((CustomListenerAdapter) o).onRelationshipRemove(this));
    }
}
