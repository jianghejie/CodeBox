package com.jcodecraeer.jcode.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.jcodecraeer.jcode.Code;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

public class UpdateChecker{
	private static final String TAG = "UpdateChecker";
	private Context mContext;
	//检查版本信息的线程
	private Thread mThread;
	//版本对比地址
	private String mCheckUrl;
	private AppVersion mAppVersion;
	//下载apk的对话框
    private	ProgressDialog mProgressDialog;
    
    private File apkFile;

	public void setCheckUrl(String url) {
		mCheckUrl = url;
	}
	
	public UpdateChecker(Context context) {
		mContext = context;
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("正在下载");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        
		    }
		});	
		mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
			}
		});	
	}
	
	public void checkForUpdates() {
		if(mCheckUrl == null) {
			//throw new Exception("checkUrl can not be null");
			return;
		}
	    final  Handler handler = new Handler(){
			public void handleMessage(Message msg) {		
				if (msg.what == 1) {
					mAppVersion = (AppVersion) msg.obj;
					try{
						int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
						if (mAppVersion.getApkCode() > versionCode) {
								showUpdateDialog();
						} else {
							//Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
						}
					}catch (PackageManager.NameNotFoundException ignored) {
						//
					} 	
				}
			}
	    };
	    
		mThread = new Thread() {
			@Override
			public void run() {
				//if (isNetworkAvailable(mContext)) {
			    Message msg = new Message();
				String json = sendPost();
				Log.i("jianghejie","json = "+json);
				if(json!=null){
					AppVersion appVersion = parseJson(json);
					msg.what = 1;
					msg.obj = appVersion;
					handler.sendMessage(msg);
				}else{
					Log.e(TAG, "can't get app update json");
				}
			}
		};
		mThread.start();
	}

	protected String sendPost() {
		HttpURLConnection uRLConnection = null;
		InputStream is = null;
		BufferedReader buffer = null;
		String result = null;
		try {
			URL url = new URL(mCheckUrl);
			uRLConnection = (HttpURLConnection) url.openConnection();
			uRLConnection.setDoInput(true);
			uRLConnection.setDoOutput(true);
			uRLConnection.setRequestMethod("POST");
			uRLConnection.setUseCaches(false);
			uRLConnection.setConnectTimeout(10 * 1000);
			uRLConnection.setReadTimeout(10 * 1000);
			uRLConnection.setInstanceFollowRedirects(false);
			uRLConnection.setRequestProperty("Connection", "Keep-Alive");
			uRLConnection.setRequestProperty("Charset", "UTF-8");
			uRLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			uRLConnection.setRequestProperty("Content-Type", "application/json");

			uRLConnection.connect();

			is = uRLConnection.getInputStream();

			String content_encode = uRLConnection.getContentEncoding();

			if (null != content_encode && !"".equals(content_encode) && content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}

			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				strBuilder.append(line);
			}
			result = strBuilder.toString();
		} catch (Exception e) {
			Log.e(TAG, "http post error", e);
		} finally {
			if(buffer!=null){
				try {
					buffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(uRLConnection!=null){
				uRLConnection.disconnect();
			}
		}
		return result;
	}

	private AppVersion parseJson(String json) {
		AppVersion appVersion = new AppVersion();
		try {	
			JSONObject obj = new JSONObject(json);
			String updateMessage = obj.getString(AppVersion.APK_UPDATE_CONTENT);
			String apkUrl = obj.getString(AppVersion.APK_DOWNLOAD_URL);
			int apkCode = obj.getInt(AppVersion.APK_VERSION_CODE);
			appVersion.setApkCode(apkCode);
			appVersion.setApkUrl(apkUrl);
			appVersion.setUpdateMessage(updateMessage);
		} catch (JSONException e) {
			Log.e(TAG, "parse json error", e);
		}
		return appVersion;
	}
	
	public void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  
        //builder.setIcon(R.drawable.icon);  
        builder.setTitle("有新版本");  
        builder.setMessage(mAppVersion.getUpdateMessage());  
        builder.setPositiveButton("下载",  
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {  
                	downLoadApk();
                }  
        });  
        builder.setNegativeButton("忽略",  
            new DialogInterface.OnClickListener() {  
                public void onClick(DialogInterface dialog, int whichButton) {  
                   
                }  
        });  
        builder.show();  		
	}
	
	public void downLoadApk() {
		String apkUrl = mAppVersion.getApkUrl();
		String dir = mContext.getExternalFilesDir( "apk").getAbsolutePath();
		File folder = Environment.getExternalStoragePublicDirectory(dir);
		if(folder.exists() && folder.isDirectory()) {
			 //do nothing 
		}else {
			folder.mkdirs();
		}
		String filename = apkUrl.substring(apkUrl.lastIndexOf("/"),apkUrl.length());
		String destinationFilePath =  dir + "/" + filename;	
		apkFile = new File(destinationFilePath);
		mProgressDialog.show();
		Intent intent = new Intent(mContext, DownloadService.class);
		intent.putExtra("url", apkUrl);
		intent.putExtra("dest", destinationFilePath);
		intent.putExtra("receiver", new DownloadReceiver(new Handler()));
		mContext.startService(intent); 
	}
	
    private class DownloadReceiver extends ResultReceiver{
        public DownloadReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                mProgressDialog.setProgress(progress);
                if (progress == 100) {
                    mProgressDialog.dismiss();
        			//如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        			String[] command = {"chmod","777",apkFile.toString()};
        			try{
	        			ProcessBuilder builder = new ProcessBuilder(command);
	        			builder.start();
	        			Intent intent = new Intent(Intent.ACTION_VIEW);
	        		    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");   
	        		    mContext.startActivity(intent);
        			}catch (Exception e){
        				
        			}
                }
            }
        }
    } 
}
