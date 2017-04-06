package com.example.user.HealthMonitor.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.example.user.HealthMonitor.R;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏标题栏以及状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉标题
        setContentView(R.layout.activity_welcome);
        handler.sendEmptyMessageDelayed(0,3000);

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
