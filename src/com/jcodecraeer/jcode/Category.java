package com.jcodecraeer.jcode;

import java.io.Serializable;

public class Category implements Serializable{
	private int ID;
	private String name;
	private String value;
	
    public final void setID(int value) {
        ID = value;
    }
    
    public final int getID() {
        return ID;
    }   
    
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
