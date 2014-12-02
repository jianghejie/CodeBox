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

public class BaseActivity extends FragmentActivity {
	private TextView mTitleTx;
	private View mBack;
	private LinearLayout contentLayout;
	private View drawerIcon;  
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initTitleBar();
	}

	public void setTitleEnabled(boolean isable){
		if(isable){
		    findViewById(R.id.titlebar_title_tab).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.titlebar_title_tab).setVisibility(View.GONE);
		}
	}
	
	public void setTitleBackGroundColor(int color){
		findViewById(R.id.common_title_bar).setBackgroundColor(color);
	}

	public void  initTitleBar(){
		mTitleTx = (TextView)findViewById(R.id.titlebar_title);		
		mBack = findViewById(R.id.titlebar_left_iv);
		drawerIcon = findViewById(R.id.drawer_icon);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				finish();  
			}
		});
	}
	
	public void setRightIcon(Icon icon) {
		IconView iconView = (IconView)findViewById(R.id.titlebar_right_1_iv);	
		iconView.setVisibility(View.VISIBLE);
		iconView.setIcon(icon);
		View rightbtn = findViewById(R.id.titlebar_right_tab);
		rightbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
		        Intent intent = new Intent(BaseActivity.this, AboutActivity.class);
		        startActivity(intent);
			}
		});
	}
	
	public void setShowDrawer(boolean f) {
		if(f) {
			drawerIcon.setVisibility(View.VISIBLE);
			mBack.setVisibility(View.GONE);
		} else {
			drawerIcon.setVisibility(View.GONE);
			mBack.setVisibility(View.VISIBLE);			
		}
	}
	
    private  void initContentView() {
        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
        content.removeAllViews();
        contentLayout=new LinearLayout(this);  
        contentLayout.setOrientation(LinearLayout.VERTICAL); 
        content.addView(contentLayout);
        LayoutInflater.from(this).inflate(R.layout.common_title_bar, contentLayout, true);
    }
    
    @Override
    public void setContentView(int layoutResID) {
        //View customContentView = LayoutInflater.from(this).inflate(layoutResID,null);
        /*this is the same result with 
         View customContentView = LayoutInflater.from(this).inflate(layoutResID,contentLayout, false);
         */
       
        //contentLayout.addView(customContentView,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        LayoutInflater.from(this).inflate(layoutResID, contentLayout, true);
        
    } 
    @Override
    public void setContentView(View customContentView) {
    	contentLayout.addView(customContentView);
        
    }     
    @Override
    public void setTitle(CharSequence title) {
    	mTitleTx.setText(title);
    }    
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return false;
		}
		return false;
	}	
}
