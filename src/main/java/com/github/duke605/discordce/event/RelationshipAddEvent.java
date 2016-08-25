package com.github.duke605.discordce.event;

import com.github.duke605.discordce.DiscordCE;
import com.github.duke605.discordce.contract.CustomListenerAdapter;
import com.github.duke605.discordce.entity.Relationship;
import org.json.JSONObject;

public class RelationshipAddEvent
{
    public Relationship relationship;

    public RelationshipAddEvent(JSONObject obj)
    {
        relationship = new Relationship(obj);
    }

    public void post()
    {
        DiscordCE.client
                .getRegisteredListeners()
                .stream()
                .filter(o -> o instanceof CustomListenerAdapter)
                .forEach(o -> ((CustomListenerAdapter) o).onRelationshipAdd(this));
    }
}
