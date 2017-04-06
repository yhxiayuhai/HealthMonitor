package com.example.user.HealthMonitor;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by user on 2017/1/12.
 */
public class LocationApplication extends Application {
    public void onCreate(){
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
