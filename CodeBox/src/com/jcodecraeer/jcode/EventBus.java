package com.jcodecraeer.jcode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.text.format.Time;


public class EventBus {
	private static EventBus mController;
    private LinkedHashMap<Integer,EventHandler> eventHandlers =
        new LinkedHashMap<Integer,EventHandler>(5);	
	
    public interface EventType {
        final int CATEGORY = 0;
        final int TOGGLE = 1;
    }
 
    private EventBus() {  
         
    }  	
	
    public interface EventHandler {
        void handleEvent(int eventType, Object obj);
    }
    public static EventBus getInstance() {
            if (mController == null) {
            	mController = new EventBus();  
            }
            return mController;
    } 
    
    public void sendEvent(int eventType,  Object obj) {
        for (Iterator<Entry<Integer, EventHandler>> handlers =
            eventHandlers.entrySet().iterator(); handlers.hasNext();) {
        Entry<Integer, EventHandler> entry = handlers.next();
        EventHandler eventHandler = entry.getValue();
        if (eventHandler != null)
        	eventHandler.handleEvent(eventType, obj);
        }
    }
    
    public void registerEventHandler(int key, EventHandler eventHandler) {
        synchronized (this) {
                eventHandlers.put(key, eventHandler);
        }
    }    
	
}