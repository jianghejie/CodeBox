package com.jcodecraeer.jcode;

import java.util.ArrayList;
import java.util.List; 

import com.jcodecraeer.jcode.fragment.CodeListFragment;
import com.jcodecraeer.jcode.fragment.NavigationDrawerFragment;
import com.jcodecraeer.jcode.update.UpdateChecker;

import be.webelite.ion.Icon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Toast;
 
public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
	static final int TAKE_PIC_REQUEST = 1;  
	private CodeListFragment mPictrueFragment;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private int albumTyle;
	public ViewController mViewController;
	/**
	 * This will not work so great since the heights of the imageViews 
	 * are calculated on the iamgeLoader callback ruining the offsets. To fix this try to get 
	 * the (intrinsic) image width and height and set the views height manually. I will
	 * look into a fix once I find extra time.
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mViewController = ViewController.getInstance();	
		setContentView(R.layout.activity_main);	
		setShowDrawer(true);
		setTitleEnabled(false);
		setRightIcon(Icon.ion_ios7_help);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		UpdateChecker updateChecker = new UpdateChecker(this);
		updateChecker.setCheckUrl("http://jcodecraeer.com/update.php");
		updateChecker.checkForUpdates();
	}
  	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		final FragmentManager fragmentManager = getSupportFragmentManager();
		final Handler handler = new Handler();
		switch(position) {
		case 0:
			if(mPictrueFragment == null) {
				mPictrueFragment = CodeListFragment.newInstance(Integer.MIN_VALUE);		
				mViewController.registerEventHandler(0, mPictrueFragment);
			} 
			handler.postDelayed(new Runnable() {
			    @Override
				public void run() {
				    fragmentManager.beginTransaction().replace(R.id.container,mPictrueFragment).commit();		
				}
			}, 500);
			break;
		}
	}
}
