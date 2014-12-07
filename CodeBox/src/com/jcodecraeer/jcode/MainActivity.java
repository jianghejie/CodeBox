package com.jcodecraeer.jcode;

import java.util.ArrayList;
import java.util.List; 

import com.jcodecraeer.jcode.fragment.CodeListFragment;
import com.jcodecraeer.jcode.fragment.NavigationDrawerFragment;
import com.jcodecraeer.jcode.update.UpdateChecker;

import be.webelite.ion.Icon;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
 
public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
	private CodeListFragment mCodeLIstFragment;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	public EventBus mEventBus;
	private DrawerLayout mDrawerLayout;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mEventBus = EventBus.getInstance();	
		setContentView(R.layout.activity_main);	
		getActionBar().setDisplayHomeAsUpEnabled(true);
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				mDrawerLayout);
		mEventBus.registerEventHandler(0, mNavigationDrawerFragment);
		UpdateChecker updateChecker = new UpdateChecker(this);
		updateChecker.setCheckUrl("http://jcodecraeer.com/update.php");
		updateChecker.checkForUpdates();
		mCodeLIstFragment = CodeListFragment.newInstance(Integer.MIN_VALUE);		
		mEventBus.registerEventHandler(1, mCodeLIstFragment);	
		getFragmentManager().beginTransaction().replace(R.id.container,mCodeLIstFragment).commit();		
	}
  	
	@SuppressLint("NewApi")
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		final FragmentManager fragmentManager = getFragmentManager();
		final Handler handler = new Handler();
		switch(position) {
		case 0:
			if(mCodeLIstFragment == null) {

			} 
			handler.postDelayed(new Runnable() {
			    @Override
				public void run() {
				   
				}
			}, 500);
			break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){		    
			case  android.R.id.home:
				mEventBus.sendEvent(EventBus.EventType.TOGGLE, null);
			    break;	
			case R.id.about:
				Intent intent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent);
				break;	
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			//restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}	
    
}
