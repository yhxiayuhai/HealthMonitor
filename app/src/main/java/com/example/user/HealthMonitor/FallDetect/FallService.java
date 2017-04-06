package com.example.user.HealthMonitor.FallDetect;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * Created by user on 2017/3/7.
 */
public class FallService extends Service {
    public static Boolean FLAG =false;//服务运行的标志
    private SensorManager sensorManager;
    private FallListener detector;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  System.out.println("Service is created");
        FLAG = true;//标记为服务正在运行
        //创建监听器类，实例化监听对象
        detector = new FallListener(this);
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(detector,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        //电源管理服务
        powerManager = (PowerManager)this.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My Tag");
        wakeLock.acquire();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        FLAG =false;//服务停止
        if (detector != null){
            sensorManager.unregisterListener(detector);
        }
    }
}
