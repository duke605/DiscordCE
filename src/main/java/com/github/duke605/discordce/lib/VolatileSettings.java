package com.github.duke605.discordce.lib;

import com.github.duke605.discordce.entity.Relationship;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class VolatileSettings {

    public static volatile HashMap<String, Relationship> relationships = new HashMap<>();

    public static boolean isBlocked(String userId)
    {
        Relationship r = relationships.get(userId);
        return r != null && r.type == Relationship.BLOCK;
    }

    public static boolean isFriend(String userId)
    {
        Relationship r = relationships.get(userId);
        return r != null && r.type == Relationship.FRIEND;
    }

    public static boolean hasIncomingFriendRequest(String userId)
    {
        Relationship r = relationships.get(userId);
        return r != null && r.type == Relationship.INCOMING;
    }

    public static boolean hasOutgoingFriendRequest(String userId)
    {
        Relationship r = relationships.get(userId);
        return r != null && r.type == Relationship.OUTGOING;
    }
}
