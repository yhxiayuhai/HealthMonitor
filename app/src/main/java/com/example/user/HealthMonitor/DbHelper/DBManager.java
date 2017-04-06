package com.example.user.HealthMonitor.DbHelper;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.user.HealthMonitor.Trace.LocationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhxia on 2017/2/10.
 */
public class DBManager {
    private LocationHelper mHelper;

    public DBManager(Context context){
        mHelper = new LocationHelper(context);
    }

    public boolean addGpsInfo(LocationInfo info) {
        boolean flag = false;
        SQLiteDatabase database = null;
        try {
            database = mHelper.getWritableDatabase();
            database.execSQL("insert into location(longitude ,latitude) values(?,?)", new String[]{info.getLongitude(), info.getLatitude()});
            flag = true;
            database.close();
        } catch (Exception e) {
            Log.i("测试", "数据库添加失败");
        }
        return flag;
    }

    public List<LocationInfo> findAll(){
        SQLiteDatabase database = null;
        List<LocationInfo> infos = new ArrayList<LocationInfo>();
        Cursor cursor = null;
        try {
            database = mHelper.getReadableDatabase();
            cursor=database.rawQuery("select longitude,latitude from location ",null);
            while (cursor.moveToNext()){
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setLongitude(cursor.getString(0));
                locationInfo.setLatitude(cursor.getString(1));
                infos.add(locationInfo);
                locationInfo = null;
            }
        } catch (Exception e){
            Log.i("测试","数据库查询失败");
        }
        return infos;
    }
}
