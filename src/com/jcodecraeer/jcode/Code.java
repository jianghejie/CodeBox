package com.jcodecraeer.jcode;

import java.io.Serializable;

public class Code implements Serializable{
	private int ID;
	private float size;
	private int viewCount;
	private int downCount;
	private int starCount;	
	private String title;
	private String uieffect;
	private String litpic;
	private String category;
	private String pluginUrl;
	private String sourceUrl;
	private String environment;
	private String updateTime;
	private String description;
	
    public final void setID(int value) {
        ID = value;
    }
    
    public final int getID() {
        return ID;
    }   
    
	public void setSize(float size) {
		this.size = size;
	}
	
	public float getSize() {
		return size;
	}
	
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	
	public int getViewCount() {
		return viewCount;
	}
	
	public void setStarCount(int starCount) {
		this.starCount = starCount;
	}
	
	public int getStarCount() {
		return starCount;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setUieffect(String uieffect) {
		this.uieffect = uieffect;
	}
	
	public String getUieffect() {
		return uieffect;
	}
	
	public void setLitpic(String litpic) {
		this.litpic = litpic;
	}
	
	public String getLitpic() {
		return litpic;
	}
	
	public void setPluginUrl(String pluginUrl) {
		this.pluginUrl = pluginUrl;
	}
	
	public String getPluginUrl() {
		return pluginUrl;
	}
	
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
	public String getSourceUrl(){
		return sourceUrl;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
