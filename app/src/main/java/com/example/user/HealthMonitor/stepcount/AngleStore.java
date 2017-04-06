package com.example.user.HealthMonitor.stepcount;

/**
 * Created by user on 2017/3/16.
 */
public class AngleStore {
    float temp;
    public float store(float angle){
         float mAngle = temp;
        temp = angle;
        return mAngle;
    }
}
