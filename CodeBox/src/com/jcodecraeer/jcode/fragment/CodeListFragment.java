package com.jcodecraeer.jcode.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jcodecraeer.PullToRefreshListView;
import com.jcodecraeer.jcode.AppContext;
import com.jcodecraeer.jcode.Code;
import com.jcodecraeer.jcode.CodeActivity;
import com.jcodecraeer.jcode.CodeListAdapter;
import com.jcodecraeer.jcode.HttpUtil;
import com.jcodecraeer.jcode.MultiItemRowListAdapter;
import com.jcodecraeer.jcode.R;
import com.jcodecraeer.jcode.ViewController;
import com.jcodecraeer.jcode.R.dimen;
import com.jcodecraeer.jcode.R.id;
import com.jcodecraeer.jcode.R.layout;
import com.jcodecraeer.jcode.ViewController.EventHandler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
 
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ActionMode;

public  class CodeListFragment extends Fragment implements OnItemClickListener,ViewController.EventHandler{
	private static final String TAG = "CodeListFragment";
	private CodeListAdapter adapter;
	private ArrayList<Code> mCodeList;

	private PullToRefreshListView mGridView;
	private int numColumns = 2;
	public static CodeListFragment newInstance(int bucketId) {
		CodeListFragment fragment = new CodeListFragment();
		Bundle args = new Bundle();
		args.putInt(Images.Media.BUCKET_ID, bucketId);
		fragment.setArguments(args);
		return fragment;
	}

	public CodeListFragment() {
		mCodeList = new ArrayList<Code>();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   // mBucketId = getArguments().getInt(Images.Media.BUCKET_ID);
	    setHasOptionsMenu(true);
	}
	
	@Override  
    public void onResume(){ 
        super.onResume();  
    }  
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		mGridView = (PullToRefreshListView)rootView.findViewById(R.id.staggeredGridView1);		
		adapter = new CodeListAdapter(getActivity(), mGridView, mCodeList);
		adapter.setOnItemClickListener(this);
        int columns = AppContext.getInstance().getPropertyInt("SETTING_COLUMNS");  
        if(columns > 1) {
        	numColumns = columns;  
        }
		int imageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
        MultiItemRowListAdapter wrapperAdapter = new MultiItemRowListAdapter(getActivity(), adapter, numColumns, imageThumbSpacing);
		mGridView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mGridView.setAdapter(wrapperAdapter);
		mGridView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadCodeList(AppContext.CODE_LIST_URL, 1, true);
			}
			public void onLoadingMore() {
				int pageIndex = mCodeList.size() / 20 + 1;
				loadCodeList(AppContext.CODE_LIST_URL, pageIndex, false);
			}
		});
		setColumnWidth();
		loadCodeList(AppContext.CODE_LIST_URL, 1, true);
		return rootView;
	}
	
	public void setType(int type) {
		adapter.notifyDataSetChanged();
	}
	
	private void loadCodeList(final String url ,final int page, final boolean isRefresh) {
	    final  Handler handler = new Handler(){
			public void handleMessage(Message msg) {		
				if (msg.what == 1) {
					ArrayList<Code> codeList = (ArrayList<Code>)msg.obj;
					if (isRefresh) {
						mCodeList.clear();	
						mGridView.onRefreshComplete (new Date().toLocaleString());
					} 
				    for (Code code : codeList) {
				    	mCodeList.add(code);
				    }
					adapter.notifyDataSetChanged();
					if (codeList.size() < 20) {
						mGridView.onLoadingMoreComplete(true);
					} else if (codeList.size() == 20) {
						mGridView.onLoadingMoreComplete(false);
					}				
					adapter.notifyDataSetChanged();
				}
			}
	    };

		new Thread() {
			public void run() {
				Message msg = new Message();
				ArrayList<Code> codeList = new ArrayList<Code>();
				try {
					codeList = parseCodeList(url, page);
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.what = 1;
				msg.obj = codeList;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private ArrayList<Code> parseCodeList(String url, final int page) {
		final ArrayList<Code> codeList = new ArrayList<Code>();
		try {
			url = HttpUtil._MakeURL(url, new HashMap<String, Object>(){{
			    put("PageNo", page);
			    put("ismobile", 1);
		    }});	
			String key = "codelist_" +  page ;
	    	String result = "";
	    	//cache
			if (HttpUtil.isNetworkConnected() && HttpUtil.isCacheDataFailure(key)) {
					result = HttpUtil.http_get(AppContext.getInstance(), url );
					HttpUtil.saveObject(result, key);
					result = (String) HttpUtil.readObject(key);	
			} else {
				result = (String) HttpUtil.readObject(key);
				if (result == null)
					result = "erro";
			}	
			try {
				
				JSONArray array = new JSONArray(result);
				JSONObject codelistobject = array.getJSONObject(0);  
				String baseUrl = AppContext.HTTP + AppContext.HOST_NAME;
				//获取代码列表
				String codeListStr  = codelistobject.getString("codelist");
				JSONArray arrayCode = new JSONArray(codeListStr);  
				for(int i=1; i<arrayCode.length() ; i++){
				   JSONObject obj = arrayCode.getJSONObject(i); 
				    Code code = new Code();
				    code.setID(i);
				    code.setTitle(obj.getString("title"));
				    code.setLitpic(baseUrl+ obj.getString("litpic"));
				    Log.i(TAG,"litpic = " + obj.getString("litpic"));
				    code.setUieffect(baseUrl+ obj.getString("uieffect"));
				    code.setPluginUrl(baseUrl + obj.getString("pluginurl").trim());
				    code.setDescription(obj.getString("codedescribe"));
				    code.setSourceUrl(obj.getString("sourceurl"));
				    codeList.add(code);						        
			   }								
			}catch (Exception e){	
				e.printStackTrace();
			} 
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return codeList;		
	}
 
	public void setColumnWidth() {
	    DisplayMetrics metric = new DisplayMetrics();
	    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
	    int width = metric.widthPixels;  
		int imageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		int columnWidth = (width / numColumns ) - imageThumbSpacing;
		AppContext.getInstance().mLoader.setRequiredSize(columnWidth);
		AppContext.getInstance().mLoader.clearCache();
	}
	 
	@Override
	public void handleEvent(int eventType) {
		if(eventType == 2){
 
		}
	}	
	
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(getActivity(), CodeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("code", mCodeList.get(position));
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
}	