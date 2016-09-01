package com.github.duke605.dce.entity;

import com.github.duke605.dce.DiscordCE;
import com.github.duke605.dce.exception.EntityNotFoundException;
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
        long time = System.currentTimeMillis();

        // Looping until user can be found
        do
        {
            user = DiscordCE.client.getUserById(obj.getJSONObject("user").getString("id"));

            // Breaking if could not find user in 30 seconds
            if (time > System.currentTimeMillis() - 30000)
                break;
        }
        while (user == null);

        // Checking if the user was found
        if (user == null)
            new EntityNotFoundException("User with id <" + obj.getJSONObject("user").get("id") + ">" +
                    " could not be found... This should not happen. All info:\n" +
                    obj).printStackTrace();
    }
}
