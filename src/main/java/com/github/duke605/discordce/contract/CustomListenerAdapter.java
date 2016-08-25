package com.github.duke605.discordce.contract;

import com.github.duke605.discordce.event.RelationshipAddEvent;
import com.github.duke605.discordce.event.RelationshipRemoveEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class CustomListenerAdapter extends ListenerAdapter {
    public void onRelationshipAdd(RelationshipAddEvent e) {}
    public void onRelationshipRemove(RelationshipRemoveEvent e) {}
}
