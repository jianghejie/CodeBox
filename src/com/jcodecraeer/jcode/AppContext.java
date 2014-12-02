package com.jcodecraeer.jcode;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.jcodecraeer.jcode.loader.ImageLoader;
 

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
/**
 * 全局应用程序类
 * 
 */
public class AppContext extends Application {
	private static final String TAG = "AppContext";
    private static AppContext instance;
    public ImageLoader mLoader;
	public static String HTTP = "http://";
	public static final String HOST_NAME = "www.jcodecraeer.com";
	public static final String CODE_LIST_URL = HTTP + HOST_NAME + "/plus/list.php?tid=31";
	@Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        mLoader = new ImageLoader(this);
    }
	
    public static AppContext getInstance() {
        return instance;
    }	
    
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}
	
	public String getPropertyString(String key){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		return settings.getString(key, "");
	}
	public int getPropertyInt(String key){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		return settings.getInt(key, 0);
	}		
	public void setPropertyString(String key ,String value){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.edit().putString(key, value).commit();
	}	
	public void setPropertyInt(String key ,int value){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.edit().putInt(key, value).commit();
	}
}
