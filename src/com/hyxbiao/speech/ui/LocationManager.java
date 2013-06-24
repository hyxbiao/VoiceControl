/*
 * Copyright (C) 2012 Baidu Inc. All rights reserved.
 */
package com.hyxbiao.speech.ui;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * 地理位置信息管理，可以获得gps信息和基站信息.
 * （百合项目暂时不上传基站信息）
 * http://developer.baidu.com/map/geosdk.htm
 * 
 * 定位依据轻量级SDK：
 * http://wiki.babel.baidu.com/twiki/bin/view/Com/Main/%E5%AE%9A%E4%BD%8D%E4%BE%9D%E6%8D%AE%E8%BD%BB%E9%87%8FSDK
 * 
 * @author fujiaxing
 *
 */
public final class LocationManager {
    /** log 开关 。 */
	private static final boolean DEBUG = false;
	/** log tag .*/
	public static final String TAG = "LocationManager";
	/** application context.*/
	private Context mContext;
	/** 位置信息管理.*/
	private static LocationManager mLocationManager;
	/** 当前location info.*/
	private LocationInfo mLastLocationInfo;
	/** 地图api location client.*/
	private LocationClient mLocationClient = null;
	
//	private TelephonyManager mTelephonyManager;
	/** location listeners.*/
	private ArrayList<LocationListener> mLocListeners;
	/** 地图定位服务名称. 需要和manifest中的对应intent-filter中的action name一致.*/
	private static final String SERVICE_NAME = "com.hyxbiao.speech.ui.location.service";
	/** 产品名称.*/
	private static final String PRODUCT_NAME = "baiduvoice_android";
	/** 坐标系.
	 * 国测局经纬度坐标系 coor=gcj02
	 * 百度墨卡托坐标系 coor=bd09
	 * 百度经纬度坐标系 coor=bd09ll 
	 * */
	private static final String COOR_TYPE = "bd09";
	/** 返回的地址文本类型.*/
	private static final String ADDR_TYPE = "detail";
	/** 默认定位周期.*/
	private static int backgroundScanSpan = (int) DateUtils.HOUR_IN_MILLIS * 5; //SUPPRESS CHECKSTYLE 
	/** 前台最新定位间隔*/
    private static int minLocationInterval = (int) DateUtils.MINUTE_IN_MILLIS * 10; //SUPPRESS CHECKSTYLE 
	/** 等待sdk定位超时时间.*/
    public static final long SDK_LOCATION_TIMEOUT = DateUtils.SECOND_IN_MILLIS * 32; //与sdk rd了解到，网络超时+内部逻辑
//	/** 从wloc服务获取位置信息的url参数.*/
//	private static final String WLOC_PARAM = "addr=country|province|city|street|city_code|district";
//	/** 通过经纬度获取地理坐标的服务器.*/
//	private static final String WLOC_SERVER = "http://loc.map.baidu.com/wloc?" + WLOC_PARAM;
//	/** 详细位置信息获取地址 .*/
//    private static final String LOCATION_SERVER = "http://api.map.baidu.com/?qt=rgc_standard";
    /** 固定精确半径为1000.*/
    private static final int FIXED_RADIUS = 1000;

    /** 后台定位周期key.*/
    public static final String KEY_BACKGROUND_SCAN_SPAN = "background_span";
    /** 前台最新定位间隔.*/
    public static final String KEY_FORGROUND_MIN_INTERVAL = "forground_min_interval";
    /** 是否正在定位中. 只针对主动发起的定位, 不算周期性的定位*/
    private boolean mIsLocating = false;
    /**上次主动定位请求时间.*/
    private long mLastRequestTime = 0;
    /** 最近定位成功与否标志*/
    private boolean mLastLocationFlag = false;
    
	/**
	 * 构造函数.
	 * @param context Activity
	 */
	private LocationManager(Context context) {
		mContext = context.getApplicationContext();
		mLocListeners = new ArrayList<LocationListener>();
		
		backgroundScanSpan = getIntPreference(KEY_BACKGROUND_SCAN_SPAN, backgroundScanSpan, context);
		minLocationInterval = getIntPreference(KEY_FORGROUND_MIN_INTERVAL, minLocationInterval, context);
		
//		mTelephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		initMapLibs();
		
		//增加cookie变化监听器
		LocSync2CookieListener listener = new LocSync2CookieListener();
		addLocationListener(listener);
	}
	
	/**
	 * 获得单例
	 * @param context Context
	 * @return {@link LocationManager}
	 */
	public static LocationManager getInstance(Context context) {
		if (mLocationManager == null) {
			mLocationManager = new LocationManager(context.getApplicationContext());
		}
		return mLocationManager;
	}
	
	/**
	 * 返回地理位置信息（基站信息是当前的，gps信息是缓存的）.
	 * @return 当前位置信息，可能为null
	 */
	public LocationInfo getLocationInfo() {
		
//		updateCellLocation();
		
		return mLastLocationInfo; // 获取的定位信息无效时，也是null
	}
    BaiduLocationListener baiduLocationListener = new BaiduLocationListener();
	
	/**
	 * 初始化地图api库, 只在示例被创建时调用.
	 */
	private void initMapLibs() {
		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(baiduLocationListener);
		
		//设置参数
		LocationClientOption option = getLocOption(backgroundScanSpan);
		mLocationClient.setLocOption(option);
		
		mIsLocating = true;
		mLastRequestTime = System.currentTimeMillis();
		mLocationClient.start();
	}
	public void release(){
	    mLocationClient.unRegisterLocationListener(baiduLocationListener);
	    mLocationClient.stop();
	}
	/**
	 * 主动请求定位
	 * @param delay 延时多长时间后再定位.
	 */
	private void requestLocation(final long delay) {
	    if (!mIsLocating
                && mLocationClient != null) {
            if (mLastLocationInfo != null
                    && System.currentTimeMillis() - mLastLocationInfo.time <= minLocationInterval) {
                    return;
            }
            if (DEBUG) {
                Log.d(TAG, "active requestLocation. current time: " + System.currentTimeMillis());
            }
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (delay > 0) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mLastRequestTime = System.currentTimeMillis();
                    int result = mLocationClient.requestLocation();
                    mLastLocationFlag = false;
                    //返回值： 0：正常发起了定位。 1：服务没有启动  2：没有监听函数。 6：请求间隔过短。 前后两次请求定位时间间隔不能小于1000ms。 
                    if (result != 0) {
                        mIsLocating = false;
                    }
                }
            };
            mIsLocating = true;
            Thread thread = new Thread(r);
            thread.setName("requestLocation");
            thread.start();
        }
        
        if (mIsLocating) {
            if (System.currentTimeMillis() - mLastRequestTime > SDK_LOCATION_TIMEOUT) {
                mIsLocating = false;
                if (DEBUG) {
                    Log.d(TAG, "onReceiveLocation not return, fixed this error.");
                }
            }
        }
	}
	/**
	 * 请求一次地理位置信息.
	 */
	public void requestLocation() {
	    requestLocation(0);
	}
	
	/**
	 * 是否处在定位过程中
	 * @return  true: 定位线程已启动，处于定位流程中
	 */
	public boolean isLocating() {
	    return mIsLocating;
	}
//	/** 改变定位周期(2.2版本sdk，直接改变周期会在当前周期最后一次完成定位后才生效，为了及时生效，先stop在start。与sdk rd了解代价不大)
//	 *  @param span 定位周期
//	 */
//	public void changeScanSpan(int span) {
//	    if (mLocationClient != null) {
//	        mLocationClient.stop();
//	        
//	        LocationClientOption option = getLocOption(span);
//	        mLocationClient.setLocOption(option);
//	        
//	        mLocationClient.start();
//	    }
//	}
	
	/**
	 * 初始化location option.
	 * @param span 定位周期.
	 * @return LocationClientOption
	 */
	private LocationClientOption getLocOption(int span) {
        //设置参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false);
        option.setAddrType(ADDR_TYPE);
        option.setCoorType(COOR_TYPE);
        option.setProdName(PRODUCT_NAME); //产品名称
        if (span < 0) {
            span = backgroundScanSpan;
        }
        
        option.setScanSpan(span); //SUPPRESS CHECKSTYLE
        option.setServiceName(SERVICE_NAME); //地图api升级，可以设置自己的服务名称，避免在自己的进程中运行时，与其它定位程序冲突
        return option;
	}
	
	/**
	 * baidu location 监听器.
	 */
	public class BaiduLocationListener implements BDLocationListener {
	    /** 定位失败重试.*/
	    private boolean canRelocation = true;
	    
	    //(特别注意：在UI线程回调）
		@Override
		public void onReceiveLocation(BDLocation location) {
		    
		    if (DEBUG) {
		        Log.d(TAG, "location spend time: " + (System.currentTimeMillis() - mLastRequestTime));
		    }
		    
			if (location == null) {
				Log.d(TAG, "BaiduLocationListener return null");
				return;
			}
			
			mLastLocationFlag = false;
			
			int errCode = location.getLocType();
			if (errCode == BDLocation.TypeGpsLocation
					|| errCode == BDLocation.TypeNetWorkLocation
					|| errCode == 65 /* 定位缓存的结果，来自地图文档.*/) {// SUPPRESS CHECKSTYLE 
			    mLastLocationFlag = true;
			    
			    // 先不赋值
			    LocationInfo locationInfo = mLastLocationInfo;
			    
			    if (locationInfo == null) {
			        locationInfo = new LocationInfo();
	            }
			    
			    locationInfo.time = System.currentTimeMillis(); //location.getTime()获取的是字符串
			    locationInfo.longitude = location.getLongitude();
			    locationInfo.latitude = location.getLatitude();
			    locationInfo.radius = location.getRadius();
			    locationInfo.addressStr = location.getAddrStr();
			    
			    locationInfo.province = location.getProvince();
			    locationInfo.city = location.getCity();
			    locationInfo.street = location.getStreet();
			    locationInfo.cityCode = location.getCityCode();
                locationInfo.district = location.getDistrict();
                locationInfo.streetNo = location.getStreetNumber();
				
				final double MAX_DIFF = 0.0001;
				if (locationInfo.longitude < MAX_DIFF
				        && locationInfo.latitude < MAX_DIFF) { //如果经纬度都为0，视为错误定位
				    mLastLocationInfo = null;
				    reLocationRequest();
				    return;
				}
				
				// 赋值
				mLastLocationInfo = locationInfo;
				
//				getWLoc();
				
				if (DEBUG) {
					Log.d(TAG, "BaiduLocationListener, "
							+ "BaiduLocationListener, " + "address: "
							+ mLastLocationInfo.addressStr + ", longitude: "
							+ mLastLocationInfo.longitude + ", latitude: "
							+ mLastLocationInfo.latitude);
				}
				
	            if (mLocListeners != null) {
	                for (LocationListener listener : mLocListeners) {
	                    listener.onReceiveLocation(mLastLocationInfo);
	                }
	            }
	            canRelocation = true;
	            
			} else if (errCode == BDLocation.TypeNetWorkException) { //63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。 
			    reLocationRequest();
			    
			} else if (errCode == BDLocation.TypeServerError) { //162~167： 服务端定位失败。 
			    Log.e(TAG, "server location error. error code:" + errCode);
			} else {
			    Log.e(TAG, "location fail. error code: " + errCode);
			}
			
//			updateCellLocation();
			mIsLocating = false;
		}
		
		/**
		 * "首次定位" 或者 "成功定位后的下一次定位" 失败(63错误、经纬度为0）后会重新定位
		 */
		private void reLocationRequest() {
            if (canRelocation) {
                canRelocation = false;
              //等待一段时间看网络是否能恢复，或者使定位失败的因素是否能就位。 前后两次请求定位时间间隔不能小于1000ms
                requestLocation(DateUtils.SECOND_IN_MILLIS);
            }
		}
        
        // < add by caohaitao ,更换 正式发布版本 lbs sdk 后 新增的接口，暂时无用 20121122
        @Override
        public void onReceivePoi(BDLocation arg0) {
            
        }
        // add by caohaitao end 
	}
	
//	/**
//	 * 从wloc获取地理位置信息.
//	 */
//	private void getWLoc() {
//	    if (mLastLocationInfo == null) {
//	        return;
//	    }
//	    
//        WLocInfoGrabberRunnable grabberRunnable = new WLocInfoGrabberRunnable(mContext, mLastLocationInfo.longitude,
//                mLastLocationInfo.latitude, mLastLocationInfo.radius); //从wloc获取地理位置信息 add by fujiaxing 20120730
//        LocSync2CookieListener listener = new LocSync2CookieListener(mContext);
//        grabberRunnable.setWlocInfoListener(listener);
//        
//        Thread thread = new Thread(grabberRunnable);
//        thread.setName("getWLocWithLongAndLat");
//        thread.start();
//        
//	}
	
	/**
	 * 更新基站位置信息。
	 */
//	private final void updateCellLocation() {
//		
//		CellLocation location = mTelephonyManager.getCellLocation();
//		
//		if (mCurerntLocationInfo == null) {
//			mCurerntLocationInfo = new LocationInfo();
//		}
//		Configuration c = mContext.getResources().getConfiguration();
//		mCurerntLocationInfo.mcc = c.mcc;
//		mCurerntLocationInfo.mnc = c.mnc;
//		
//        if (location instanceof GsmCellLocation) {
//            GsmCellLocation loc = (GsmCellLocation)location;
//            mCurerntLocationInfo.lac = loc.getLac();
//            mCurerntLocationInfo.cid = loc.getCid();
//            
//        } else if (location instanceof CdmaCellLocation) {
//            CdmaCellLocation loc = (CdmaCellLocation)location;
//            int bid = loc.getBaseStationId();
//            int sid = loc.getSystemId();
//            int nid = loc.getNetworkId();
//        } else {
//
//        }
//    }
	
	/**
	 * 获取当前location信息.
	 * @author fujiaxing
	 *
	 */
	public static class LocationInfo {
		/** 当前时间.*/
		public long time;
		/** 经度.*/
		public double longitude;
		/** 维度.*/
		public double latitude;
		/** 精度半径 .*/
		public double radius;
		/** 地址.*/
		public String addressStr;
		/** 省份.*/
		public String province;
		/** 城市.*/
		public String city;
		/** 街道.*/
		public String street;
		/** 街道号.*/
		public String streetNo;
		/** 区县.*/
		public String district;
		/** 城市编码.*/
		public String cityCode;
		/** 基站id.*/
//		int cid;
//		/** locale area code.*/
//		int lac;
//		/** mobile counry code.*/
//		int mcc;
//		/** mobile network code.*/
//		int mnc;

	     /**
         * toString
         * @return String
         */
        @Override
		public String toString() {
            return "LocationInfo [time=" + time + ", longitude=" + longitude
                    + ", latitude=" + latitude + ", radius=" + radius
                    + ", addressStr=" + addressStr + ", province=" + province
                    + ", city=" + city + ", street=" + street + ", streetNo="
                    + streetNo + ", district=" + district + ", cityCode="
                    + cityCode + "]";
        }
	}
	
	/**
	 * 监听位置信息变化.
	 * @author fujiaxing
	 *
	 */
	interface LocationListener {
		
		/**
		 * 当得到位置信息后被调用
		 * @param locationInfo {@link LocationInfo}
		 */
		void onReceiveLocation(LocationInfo locationInfo);
	}
	
	/**
	 * 增加位置信息监听listener,不用时，需要清除.
	 * @param listener {@link LocationListener}
	 */
	public void addLocationListener(LocationListener listener) {
		if (!mLocListeners.contains(listener)) {
			mLocListeners.add(listener);
		}
	}
	
	/**
	 * 删除位置信息监听器
	 * @param listener {@link LocationListener}
	 */
	public void delLocationListener(LocationListener listener) {
		mLocListeners.remove(listener);
	}
	
	
//	/**
//	 * 从wloc服务器获取地理位置信息
//	 * @author fujiaxing
//	 *
//	 */
//	class WLocInfoGrabberRunnable implements Runnable {
//
//        /**Context*/
//        private Context context;
//        /**经度*/
//	    private double longitude;
//        /**维度*/
//	    private double latitude; 
//        /**经度半径*/
//	    private double radius;
//	    /** wloc info listener.*/
//	    private WLocInfoListener mWLocInfoListener;
//	    /**
//	     * construct method.
//	     * @param ctx Context
//	     * @param longit 经度
//	     * @param lat 维度
//	     * @param r 经度半径
//	     */
//	    public WLocInfoGrabberRunnable(Context ctx, double longit, double lat, double r) {
//	        context = ctx;
//	        longitude = longit;
//	        latitude = lat;
//	        radius = r;
//	    }
//	    
//        @Override
//        public void run() {
//            String url = LOCATION_SERVER + "&x=" + longitude + "&y=" + latitude + "&dis=" + radius; //加经纬度
//            if (DEBUG) {
//                Log.d(TAG, "WLocInfoGrabberRunnable, url: " + url);
//            }
//            url = BaiduIdentityManager.getInstance(context).processUrl(url);
//            HttpGet httpget = new HttpGet(url);
//            InputStream inputStream = null;
//            ProxyHttpClient httpClient = Utility.createHttpClient(context);
//            try {
//                HttpResponse response = httpClient.execute(httpget);
//                HttpEntity resEntity = response.getEntity();
//                if (inputStream == null) {
//                    inputStream = resEntity.getContent();
//                }
//                
//                parseData(inputStream, longitude, latitude);
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                httpClient.close();
//            }
//        }
//	    
//        /**
//         * 解析wloc server返回的json数据.
//         * @param inputStream InputStream
//         * @param longitude 经度
//         * @param latitude 维度
//         */
//        private void parseData(InputStream inputStream, double longitude, double latitude) {
//            final String content = "content";
//            final String keyAdrrDetail = "address_detail";
//            final String keyProvince = "province";
//            final String keyCity = "city";
//            final String keyDistrict = "district";
//            final String keyStreet = "street";
//            final String keyStreetNumber = "street_number";
//            final String keyCiteCode = "city_code";
//            
//            String jsonStr = Utility.getStringFromInput(inputStream);
//            if (DEBUG) {
//                Log.d(TAG, "WLocInfoGrabberRunnable, server return: " + jsonStr);
//            }
//            try {
//                JSONObject jsonObject = new JSONObject(jsonStr);
//                JSONObject contentObj = jsonObject.getJSONObject(content);
//                JSONObject addrDetailObj = contentObj.getJSONObject(keyAdrrDetail);
//                
//                
//                //验证当前的经纬度还是否是请求wloc时的经纬度
//                final double equThreshold = 0.01; 
//                if (mLastLocationInfo != null
//                        && Math.abs(mLastLocationInfo.longitude - longitude) < equThreshold
//                        && Math.abs(mLastLocationInfo.latitude - latitude) < equThreshold) { 
//                    mLastLocationInfo.province = addrDetailObj.getString(keyProvince);
//                    mLastLocationInfo.city = addrDetailObj.getString(keyCity);
//                    mLastLocationInfo.street = addrDetailObj.getString(keyStreet);
//                    mLastLocationInfo.cityCode = addrDetailObj.getString(keyCiteCode);
//                    mLastLocationInfo.district = addrDetailObj.getString(keyDistrict);
//                    mLastLocationInfo.streetNo = addrDetailObj.getString(keyStreetNumber);
//                    
//                    if (DEBUG) {
//                        Log.d(TAG, mLastLocationInfo.toString());
//                    }
//                    if (mWLocInfoListener != null) {
//                        mWLocInfoListener.onReceiveLocation(mLastLocationInfo);
//                    }
//                }
//                
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        
//        /**
//         * 增加wloc info listener.
//         * @param listener {@link WLocInfoListener}
//         */
//        public void setWlocInfoListener(WLocInfoListener listener) {
//            mWLocInfoListener = listener;
//        }
//        
//        /**
//         * 增加wloc info listener.
//         */
//        public void delWlocInfoListener() {
//            mWLocInfoListener = null;
//        }
//	}

//	/**
//	 * 
//	 * @author fujiaxing
//	 */
//    interface WLocInfoListener {
//        /**
//         * 当得到位置信息后被调用
//         * @param locationInfo {@link LocationInfo}
//         */
//        void onReceiveLocation(LocationInfo locationInfo);
//    }
    
    /**
     * 定位周期。
     * @author fujiaxing
     *
     */
    public static class LocationPeriod {
        /** 后台扫描周期.*/
        public int backgroundPeriod;
        /** 前台最新定位间隔.*/
        public int forgroundMinInterval;
    }
    
    
    /**
     * 获得server下发的long值.
     * @param name Key
     * @param defValue DEF VALUE
     * @param context Context
     * @return 无对应key，则返回false
     */
    public static int getIntPreference(String name, int defValue, Context context) {
        SharedPreferences preference = PreferenceManager
        .getDefaultSharedPreferences(context.getApplicationContext());
        return preference.getInt(name, defValue);
    }
    
    /**
     * 获取最近一次定位是否成功标志
     * @return 成功标志
     */
    public boolean getLastLocationFlag() {
        return mLastLocationFlag;
    }
}
