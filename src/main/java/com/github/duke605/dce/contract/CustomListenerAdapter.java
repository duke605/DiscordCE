package com.github.duke605.dce.contract;

import com.github.duke605.dce.event.RelationshipAddEvent;
import com.github.duke605.dce.event.RelationshipRemoveEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class CustomListenerAdapter extends ListenerAdapter {
    public void onRelationshipAdd(RelationshipAddEvent e) {}
    public void onRelationshipRemove(RelationshipRemoveEvent e) {}
}
