package com.jcodecraeer.jcode;
import be.webelite.ion.Icon;
import be.webelite.ion.IconView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseActivity extends Activity {
	private TextView mTitleTx;
	private View mBack;
	private LinearLayout contentLayout;
	private View drawerIcon;  
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
	}
 
}
