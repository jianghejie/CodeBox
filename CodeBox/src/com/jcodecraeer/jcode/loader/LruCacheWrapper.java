package com.jcodecraeer.jcode.loader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
/**
 *
 * @author hejie
 * LruCache的包装类
 *
 */
public class LruCacheWrapper {
    private LruCache<String, Bitmap> cache;
 
    public LruCacheWrapper(){
		int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        int mCacheSize = maxMemory / 4;
        cache = new LruCache<String, Bitmap>(mCacheSize){

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
			
		};
    }
    
    public Bitmap get(String key){
    	return cache.get(key);
    }

    public void put(String key, Bitmap bitmap){
        if(cache.get(key)!= null && bitmap != null){
        	cache.put(key, bitmap);  
        }   
    }
    public void clear() {
 
    }

}
