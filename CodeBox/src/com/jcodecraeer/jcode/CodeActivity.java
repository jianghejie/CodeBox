package com.jcodecraeer.jcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List; 

import com.jcodecraeer.jcode.update.DownloadService;
import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
 
public class CodeActivity extends BaseActivity {
	private static final String TAG = "CodeActivity";
    private Button runButton;
	public ViewController mViewController;
    private	ProgressDialog mProgressDialog;
    private Code mCode;
    private TextView titleText;
    private TextView sourceUrlText;
    private TextView codeDescriptionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mViewController = ViewController.getInstance();	
		setContentView(R.layout.activity_code);	
		mCode =(Code)getIntent().getSerializableExtra("code");
		setTitle(mCode.getTitle());
		
		//retive views
		titleText = (TextView)findViewById(R.id.title);
		sourceUrlText = (TextView)findViewById(R.id.source);
		codeDescriptionText = (TextView)findViewById(R.id.descrip);
		titleText.setText(mCode.getTitle());
		sourceUrlText.setText(mCode.getSourceUrl());
		codeDescriptionText.setText(mCode.getDescription());
		
		runButton = (Button)findViewById(R.id.run);
		runButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				downLoadPlugin();
			}
		});
		sourceUrlText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				if(sourceUrlText.getText()!= null && !sourceUrlText.getText().equals("")){
					Uri uri = Uri.parse(sourceUrlText.getText().toString());
			        Intent it = new Intent(Intent.ACTION_VIEW, uri);
			        it.setData(uri); 
			        it.setAction( Intent.ACTION_VIEW); 
			        startActivity(it); 						
				}
			}
		});
		
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("正在下载");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
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
  
	public void downLoadPlugin() {
		String pluginUrl = mCode.getPluginUrl();
	    if(Utils.isExternalStorageWritable()) {
			String dir = getExternalFilesDir( "plugin").getAbsolutePath();
			File folder = Environment.getExternalStoragePublicDirectory(dir);
			if(folder.exists() && folder.isDirectory()) 
			{
				  
			}else {
				folder.mkdirs();
			}
			String filename = pluginUrl.substring(pluginUrl.lastIndexOf("/"),pluginUrl.length());
			Log.i(TAG,"filename = " + filename);
			String destinationFilePath =  dir + "/" + filename;	
			File file = new File(destinationFilePath);
			if(file.exists() && file.isFile()){
				loadPluginToHost();
				return;
			}
			mProgressDialog.show();
			Intent intent = new Intent(this, DownloadService.class);
			intent.putExtra("url", pluginUrl);
			intent.putExtra("dest", destinationFilePath);
			intent.putExtra("receiver", new DownloadReceiver(new Handler()));
			startService(intent); 	    	
	    }else {
	    	Toast.makeText(this, "无法获得读写权限（是不是手机连着电脑，拔下来试试）", 300).show();
	    	return;
	    }
	}
	
	public void loadPluginToHost() {
		String pluginUrl = mCode.getPluginUrl();
		String dir = getExternalFilesDir( "plugin").getAbsolutePath();
		String filename = pluginUrl.substring(pluginUrl.lastIndexOf("/"),pluginUrl.length());
		File plugin = new File (dir,  filename);
        PluginItem item = new PluginItem();
        item.pluginPath = plugin.getAbsolutePath();
        item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);
        if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0) {
            item.launcherActivityName = item.packageInfo.activities[0].name;
        }
        DLPluginManager pluginManager = DLPluginManager.getInstance(this);
        pluginManager.loadApk(item.pluginPath);
        pluginManager.startPluginActivity(this, new DLIntent(item.packageInfo.packageName, item.launcherActivityName));	        
	}
	
    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String launcherActivityName;
        public PluginItem() {
        }
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
                	Log.i("progress","progress = 100");
                	loadPluginToHost();
                    mProgressDialog.dismiss();
                }
            }
        }
    }    
}
