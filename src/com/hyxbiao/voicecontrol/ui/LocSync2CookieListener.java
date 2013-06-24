/*
 * Copyright (C) 2012 Baidu Inc. All rights reserved.
 */

package com.hyxbiao.voicecontrol.ui;

import com.baidu.android.speech.Location;
import com.baidu.android.speech.SpeechConfig;
import com.hyxbiao.voicecontrol.ui.LocationManager.LocationInfo;
import com.hyxbiao.voicecontrol.ui.LocationManager.LocationListener;

import android.util.Log;

/**
 * 监听地理位置信息变化，将变化写入baidu.com cookie中.
 * 
 * @author fujiaxing
 * @since 2012-7-30
 */
public class LocSync2CookieListener implements LocationListener {
    private static final String LOG_TAG = "LocSync2CookieListener";

    /**
     * construct method.
     * 
     * @param context Context
     */
    public LocSync2CookieListener() {
    }

    @Override
    public void onReceiveLocation(LocationInfo locationInfo) {
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, locationInfo.toString());
        }
        Location location = getGeoLocation(locationInfo);
        SpeechConfig.setLocation(location);
    }

    /**
     * 返回地理位置信息GeoLocation
     * 
     * @param context Context
     * @param locationInfo {@link LocationInfo}
     * @return GeoLocation
     */
    private Location getGeoLocation(LocationInfo locationInfo) {
        Location location = new Location();
        location.setAddressStr(locationInfo.addressStr);
        location.setLongitude(locationInfo.longitude);
        location.setLatitude(locationInfo.latitude);
        location.setRadius(locationInfo.radius);
        location.setCity(locationInfo.city);
        location.setDistrict(locationInfo.district);
        location.setStreet(locationInfo.street);
        location.setCityCode(locationInfo.cityCode);
        location.setStreetNo(locationInfo.streetNo);
        return location;
    }
}
