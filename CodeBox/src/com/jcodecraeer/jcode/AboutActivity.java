package com.jcodecraeer.jcode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
 
public class AboutActivity extends BaseActivity {
    private Button runButton;
	public EventBus mViewController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mViewController = EventBus.getInstance();	
		setContentView(R.layout.activity_about);	
		setTitle("关于");
		runButton = (Button)findViewById(R.id.run);
		runButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				Uri uri = Uri.parse("http://www.jcodecraeer.com");
		        Intent it = new Intent(Intent.ACTION_VIEW, uri);
		        it.setData(uri); 
		        it.setAction( Intent.ACTION_VIEW); 
		        startActivity(it); 				 
			}
		});	 
	}
}
