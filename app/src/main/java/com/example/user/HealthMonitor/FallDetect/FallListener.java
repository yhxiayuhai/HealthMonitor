package com.example.user.HealthMonitor.FallDetect;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.user.HealthMonitor.util.Config;

import java.util.ArrayList;

/**
 * Created by user on 2017/3/7.
 */
public class FallListener implements SensorEventListener {
    Context context;
    int count = 0;
    ArrayList<Double> DataStore;
    boolean flag = false;
    float[][] DataBef = new float[200][3];
    public FallListener(Context context){
        super();
        this.context = context;
        DataStore = new ArrayList<Double>();
    }
    double[] sample = Config.SAMPLE;

    @Override
    public void onSensorChanged(SensorEvent event){
        float[] value = event.values;
        if (flag) {
            for (int j = 0; j < 3; j++) {
                DataBef[count][j] = value[j];
            }
            count++;
            if (count == 200) {
                DataCalculate(DataBef);
                flag = false;
                count = 0;
                DataBef = null;
                DataBef = new float[200][3];
            }
        }
        else {
            double acc = 0;
            for (int i = 0; i < 3; i++) {
                acc = acc + value[i] * value[i];
            }
            acc = Math.sqrt(acc);
            if (acc<3.7)
                flag = true;
        }
    }

    public void DataCalculate(float[][] Data_in){
        double svm = 0;
        double[]  DataAft = new double[200];
        for (int i=0; i<200; i++){
            for (int j=0; j<3; j++){
                svm = svm + Data_in[i][j]*Data_in[i][j];
            }
            svm = Math.sqrt(svm);
            DataAft[i] = svm;
            svm = 0;
        }
        FallDetect(DataAft);
    }

    public void FallDetect(double[] query){
        DTW dtw = new DTW(sample,query);
        double similirity = dtw.warpingDistance;
        if (similirity < 6){
            Alert();
        }
    }

    //检测到摔倒，用户判断时间
    public void Alert() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MainTab");
        intent.putExtra("msg",true);
        context.sendBroadcast(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
}
