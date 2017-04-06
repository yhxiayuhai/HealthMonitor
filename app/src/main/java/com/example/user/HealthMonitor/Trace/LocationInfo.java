package com.example.user.HealthMonitor.Trace;

/**
 * Created by yhxia on 2017/2/11.
 */
public class LocationInfo {
    public String latitude;
    public String longitude;

    public LocationInfo(){}

    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Location [longitude=" + longitude + ", latitude=" + latitude
                + "]";
    }
}
