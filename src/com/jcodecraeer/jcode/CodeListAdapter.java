package com.jcodecraeer.jcode;

import java.util.ArrayList;

import com.jcodecraeer.PullToRefreshListView;
import com.jcodecraeer.jcode.loader.ImageLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CodeListAdapter extends BaseAdapter {
    private static final String TAG = "ImageGridAdapter";
    private static final boolean DEBUG = true;
	private ImageLoader mLoader;
	private ArrayList<Code> mCodeList;
	private LayoutInflater mLayoutInflater;
    private PullToRefreshListView mGridView;
    private OnItemClickListener mOnItemClickListener;   
    private Context mContext;
	public CodeListAdapter(Context context,  PullToRefreshListView gridView,
			ArrayList<Code> list) {
		mLoader = AppContext.getInstance().mLoader;
		mLoader.setNeedCropSquareBitmap(false);
		mCodeList = list;
		mLayoutInflater = LayoutInflater.from(context); 
		mGridView = gridView;
		mContext = context;
	}
	
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
	
	public int getCount() {
		return mCodeList.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}
    
	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        if (DEBUG) Log.i(TAG, "position = " + position);
        
		ViewHolder holder = null;
		if (convertView == null) { 
			convertView = mLayoutInflater.inflate(R.layout.item_code_list, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView .findViewById(R.id.imageView);
			holder.title = (TextView)convertView .findViewById(R.id.code_title);
	        if(holder.imageView.getLayoutParams().width != mLoader.getRequiredSize() || holder.imageView.getLayoutParams().height != mLoader.getRequiredSize() ) {
	            if (DEBUG)
	                Log.i(TAG, "change imageview LayoutParams ");
	            LayoutParams params = holder.imageView.getLayoutParams();  
	            params.height=mLoader.getRequiredSize() * 3 /2;  
	            params.width =mLoader.getRequiredSize();  
	            holder.imageView.setLayoutParams(params);    
	        } 
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		mLoader.DisplayImage(mCodeList.get(position).getLitpic(), holder.imageView);
		holder.title.setText(mCodeList.get(position).getTitle());
        final View clickedView = convertView;
        // set the on click listener for each of the items
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(null, clickedView, position, position);
                }
            }
        });
		return convertView;
	}
 
	static class ViewHolder {
		 ImageView imageView;
		 TextView title;
	}
}
