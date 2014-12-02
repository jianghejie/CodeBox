package com.jcodecraeer.jcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


public class HttpUtil {
	private static final String TAG = "HttpUtil";
	public static final String UTF_8 = "UTF-8";
	public static final String GBK = "GBK";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;
	
	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap网络 */
	public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;
	
	private static final int CACHE_TIME = 10 * 60000;// 缓存失效时间
	
	public static boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnected();
	}

	/**
	 * 获取网络状态，wifi,wap,2g,3g.
	 *
	 * @param context 上下文
	 * @return int 网络状态 {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G},          *{@link #NETWORKTYPE_INVALID},{@link #NETWORKTYPE_WAP}* <p>{@link #NETWORKTYPE_WIFI}
	 */

	public static int getNetWorkType(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        int netWorkType = NETWORKTYPE_INVALID;
		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();

			if (type.equalsIgnoreCase("WIFI")) {
				netWorkType = NETWORKTYPE_WIFI;
				Log.i("NETWORKTYPE_WIFI","NETWORKTYPE_WIFI");
            } else if (type.equalsIgnoreCase("MOBILE")) {
            	String proxyHost = android.net.Proxy.getDefaultHost();

            	netWorkType = TextUtils.isEmpty(proxyHost)
            			? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)
            			: NETWORKTYPE_WAP;
            }
		} else {
			netWorkType = NETWORKTYPE_INVALID;
		}

		return netWorkType;
	} 
	
	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			switch (telephonyManager.getNetworkType()) {
		        case TelephonyManager.NETWORK_TYPE_1xRTT:
		            return false; // ~ 50-100 kbps
		        case TelephonyManager.NETWORK_TYPE_CDMA:
		            return false; // ~ 14-64 kbps
		        case TelephonyManager.NETWORK_TYPE_EDGE:
		            return false; // ~ 50-100 kbps
		        case TelephonyManager.NETWORK_TYPE_EVDO_0:
		            return true; // ~ 400-1000 kbps
		        case TelephonyManager.NETWORK_TYPE_EVDO_A:
		            return true; // ~ 600-1400 kbps
		        case TelephonyManager.NETWORK_TYPE_GPRS:
		            return false; // ~ 100 kbps
		        case TelephonyManager.NETWORK_TYPE_HSDPA:
		            return true; // ~ 2-14 Mbps
		        case TelephonyManager.NETWORK_TYPE_HSPA:
		            return true; // ~ 700-1700 kbps
		        case TelephonyManager.NETWORK_TYPE_HSUPA:
		            return true; // ~ 1-23 Mbps
		        case TelephonyManager.NETWORK_TYPE_UMTS:
		            return true; // ~ 400-7000 kbps
		        case TelephonyManager.NETWORK_TYPE_EHRPD:
		            return true; // ~ 1-2 Mbps
		        case TelephonyManager.NETWORK_TYPE_EVDO_B:
		            return true; // ~ 5 Mbps
		        case TelephonyManager.NETWORK_TYPE_HSPAP:
		            return true; // ~ 10-20 Mbps
		        case TelephonyManager.NETWORK_TYPE_IDEN:
		            return false; // ~25 kbps
		        case TelephonyManager.NETWORK_TYPE_LTE:
		            return true; // ~ 10+ Mbps
		        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
		            return false;
		        default:
		            return false;
				}
	}	
	
	public static void cleanCookie() {
		appCookie = "";
	}
	private static String getCookie(AppContext appContext) {
		if(appCookie == null || appCookie == "") {
			appCookie = appContext.getPropertyString("cookie");
		}
		return appCookie;
	}
	
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("www.jcodecraeer.com");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
			ua.append("/"+android.os.Build.MODEL); //手机型号
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
		
	private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		//httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	
	
	private static GetMethod getHttpGet(String url,String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", "www.jcodecraeer.com");
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url,String cookie, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", "www.jcodecraeer.com");
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);

		return httpPost;
	}
	
	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if(url.indexOf("?")<0)
			url.append('?');

		for(String name : params.keySet()){
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			//不做URLEncoder处理
			//url.append(URLEncoder.encode(String.valueOf(params.get(name)), UTF_8));
		}

		return url.toString().replace("?&", "?");
	}
	
	/**
	 * get请求URL
	 * @param url
	 * @throws AppException 
	 */
	public static String http_get(AppContext appContext,String url) throws AppException {
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);		
		HttpClient httpClient = null;
		GetMethod httpGet = null;		
		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url,cookie,userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				Log.i("http","url="+url);
				Header[] headers = httpGet.getRequestHeaders();
			      for(int i=0 ;i<headers.length ;i++){
			    	  Header header  = headers[i];
			    	 
			    	  Log.d(TAG, header.getName()+" = "+header.getValue());
			    }
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}else if(statusCode == HttpStatus.SC_OK){
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //保存cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setPropertyString("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}	

		        }				
		     	responseBody = httpGet.getResponseBodyAsString();  
		     	Log.i(TAG,"responseBody = " + responseBody);
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.http(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		return responseBody;
	}

    

	/**
	 * 公用post方法
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	public static String http_post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException {
		//System.out.println("post_url==> "+url);		
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		//post表单参数处理
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null)
        for(String name : params.keySet()){
        	parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
        	//System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
        }
        if(files != null)
        for(String file : files.keySet()){
        	try {
				parts[i++] = new FilePart(file, files.get(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	//System.out.println("post_key_file==> "+file);
        }
		
		String responseBody = "";
		int time = 0;
		do{
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url,cookie,userAgent);	        
		        httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		       
		        if(statusCode != HttpStatus.SC_OK) 
		        {
		        	throw AppException.http(statusCode);
		        }
		        else if(statusCode == HttpStatus.SC_OK){
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            
		            //保存cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setPropertyString("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}	
		        }
		        
		     	BufferedReader reader = new BufferedReader(new InputStreamReader(httpPost.getResponseBodyAsStream()));
		     	StringBuffer stringBuffer = new StringBuffer();
		     	String str = "";
		     	while((str = reader.readLine())!=null){
		     		stringBuffer.append(str);
		     	}
		     	responseBody = stringBuffer.toString();
		        //System.out.println("XMLDATA=====>"+responseBody);
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.http(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);  
        return responseBody;
	}
	
	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public static boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = AppContext.getInstance().getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public static boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = AppContext.getInstance().openFileOutput(file, AppContext.getInstance().MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Serializable readObject(String file) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = AppContext.getInstance().openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
	    }
		return null;
	}		
}
