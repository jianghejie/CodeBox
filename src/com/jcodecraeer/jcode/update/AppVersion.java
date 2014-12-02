package com.jcodecraeer.jcode.update;

public class AppVersion {
	private String updateMessage;
	private String apkUrl;
	private int apkCode;
	public static final String APK_DOWNLOAD_URL = "url";
	public static final String APK_UPDATE_CONTENT = "updateMessage";
	public static final String APK_VERSION_CODE = "versionCode";
	
	public void setUpdateMessage(String updateMessage) {
		this.updateMessage = updateMessage;
	}
	
	public String getUpdateMessage() {
		return updateMessage;
	}
	
	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}
	
	public String getApkUrl() {
		return apkUrl;
	}
	
	public void setApkCode(int apkCode) {
		this.apkCode = apkCode;
	}
	
	public int getApkCode() {
		return apkCode;
	}
}
