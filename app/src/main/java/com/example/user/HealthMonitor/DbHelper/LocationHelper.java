package com.example.user.HealthMonitor.DbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yhxia on 2017/2/10.
 */
public class LocationHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="location.db";
    private static final int SCHEMA_VERSION = 1;
    public static final String TABLE_NAME="LocationTable";

    public LocationHelper(Context context){
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE location(_id integer primary key autoincrement," +
                "longitude TEXT,latitude TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
