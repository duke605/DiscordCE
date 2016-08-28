package com.github.duke605.dce.handler;

import com.github.duke605.dce.event.RelationshipAddEvent;
import com.github.duke605.dce.event.RelationshipRemoveEvent;
import net.dv8tion.jda.client.entities.impl.JDAClientImpl;
import net.dv8tion.jda.client.requests.WebSocketExtension;
import net.dv8tion.jda.requests.WebSocketCustomHandler;
import org.json.JSONObject;

public class CustomWebSocketHandler implements WebSocketCustomHandler  {

    private WebSocketExtension wse;
    private JDAClientImpl api;

    public CustomWebSocketHandler(JDAClientImpl api)
    {
        this.api = api;
        this.wse = new WebSocketExtension(api);
    }

    @Override
    public boolean handle(JSONObject raw) {
        if (raw.getInt("op") != 0)
            return false;

        if (wse.handle(raw))
            return true;

        String type = raw.getString("t");
        JSONObject content = raw.getJSONObject("d");

        switch (type) {
            case "RELATIONSHIP_ADD":
                new RelationshipAddEvent(content).post();
                return true;
            case "RELATIONSHIP_REMOVE":
                new RelationshipRemoveEvent(content).post();
                return false;
        }

        return false;
    }
}
