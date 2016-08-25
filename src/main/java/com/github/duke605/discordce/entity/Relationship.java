package com.github.duke605.discordce.entity;

import com.github.duke605.discordce.DiscordCE;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

public class Relationship
{
    public static final int FRIEND = 1;
    public static final int BLOCK = 2;
    public static final int INCOMING = 3;
    public static final int OUTGOING = 4;

    public int type;
    public String id;
    public User user;

    public Relationship(JSONObject obj) {
        type = obj.getInt("type");
        id = obj.getString("id");

        user = DiscordCE.client.getUserById(obj.getJSONObject("user").getString("id"));
    }
}
