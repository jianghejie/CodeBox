package com.jcodecraeer.jcode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.text.format.Time;


public class ViewController {
	private static ViewController mController;
    private LinkedHashMap<Integer,EventHandler> eventHandlers =
        new LinkedHashMap<Integer,EventHandler>(5);	
	public interface ViewType {
        final int TOP_CATEGORY = -1;
        final int CATEGORY_IN_PAGER = 0;
        final int TAGS = 1;
        final int QUESTION = 2;
        final int CNBETA = 3;
        final int SETTING = 4;
        final int MEMBERCENTER = 5;
    }
 
    public interface EventHandler {
        void handleEvent(int eventType);
    }
    public static ViewController getInstance() {
            if (mController == null) {
            	mController = new ViewController();  
            }
            return mController;
    } 
    private ViewController() {
 
    }
    public void sendEvent(int eventType) {
        for (Iterator<Entry<Integer, EventHandler>> handlers =
            eventHandlers.entrySet().iterator(); handlers.hasNext();) {
        Entry<Integer, EventHandler> entry = handlers.next();
        EventHandler eventHandler = entry.getValue();
        if (eventHandler != null)
        	eventHandler.handleEvent(eventType);
        }
    }
    
    public void registerEventHandler(int key, EventHandler eventHandler) {
        synchronized (this) {
                eventHandlers.put(key, eventHandler);
        }
    }    
	
}