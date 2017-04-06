package com.example.user.HealthMonitor.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.user.HealthMonitor.DbHelper.DBManager;
import com.example.user.HealthMonitor.FallDetect.FallService;
import com.example.user.HealthMonitor.R;
import com.example.user.HealthMonitor.Trace.LocationInfo;
import com.example.user.HealthMonitor.info.UserAction;
import com.example.user.HealthMonitor.stepcount.HistoryStep;
import com.example.user.HealthMonitor.stepcount.StepArcView;
import com.example.user.HealthMonitor.stepcount.StepCounterService;
import com.example.user.HealthMonitor.stepcount.StepDetector;
import com.example.user.HealthMonitor.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016/11/28.
 */
public class MainTabActivity extends Activity {
    private static final String TAG = "MainActivity";   //日志的TAG
    private TextView track,steps,information,alarm,calorie,distance;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ActionBar actionBar;
    private StepArcView cc;
    private MapView mMapView = null;
    private BaiduMap baiduMap = null;
    private Marker marker = null;
    private BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.location_arrows);
    private LocationClient locationClient = null;
    boolean isFirstLoc = true;
    private int total_step = 0;
    private Vibrator vibrator;
    AudioManager aManager;
    MediaPlayer player;
    private Dialog mDialog;
    private Handler mOffHandle;
    private TextView mOffTextView;
    private Timer mOffTime;
    private DBManager dbManager;
    String LocationStatus = null;
    Thread thread;


   private BroadcastReceiver receiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           boolean fallStatus = intent.getBooleanExtra("msg", false);
           if (fallStatus) {
              Alert();
           }
       }
   };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_maintab);
        vibrator = (Vibrator)this.getSystemService(VIBRATOR_SERVICE);
        aManager=(AudioManager)getSystemService(Service.AUDIO_SERVICE);
        mMapView = (MapView)findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myListener); //注册监听函数
        startService(new Intent(this, StepCounterService.class));
        startService(new Intent(this,FallService.class));
        dbManager = new DBManager(this);
        setLocationOption(); //设置定位参数
        requestLocation();
        initArcView();
        initActionBar();
        initDrawerLayout();
        registerReceiver();
        ToggleButton tb = (ToggleButton)findViewById(R.id.tb);
        Log.i("Alarm", "Alarm");
    }

    public void registerReceiver(){
        IntentFilter filter =new IntentFilter();
        filter.addAction("android.intent.action.MainTab");
        registerReceiver(receiver,filter);
    }

    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            Log.d(TAG, "BDLocationListener -> onReceiveLocation");
            //mapView销毁后不在处理新接收的位置
            if(bdLocation == null || mMapView == null) {
                Log.d(TAG, "BDLocation or mapView is null");
                return;
            }
            if(!bdLocation.getLocationDescribe().isEmpty()) {
            }else if (bdLocation.hasAddr()) {
            }else {
                Log.d(TAG, "BDLocation has no addr info");
                return;
            }
            //以下打印日志，打印一些详细信息，供参考
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("\nTime : " + bdLocation.getTime());            //服务器返回的当前定位时间
            sBuilder.append("\nError code : " + bdLocation.getLocType());   //定位结果码
            sBuilder.append("\nLatitude : " + bdLocation.getLatitude());    //获取纬度坐标
            sBuilder.append("\nLongtitude : " + bdLocation.getLongitude()); //获取经度坐标
            sBuilder.append("\nRadius : " + bdLocation.getRadius());        //位置圆心
            //根据定位结果码判断是何种定位以及定位请求是否成功
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                //GPS定位结果
                sBuilder.append("\nSpeed : " + bdLocation.getSpeed());//当前运动的速度
                sBuilder.append("\nSatellite number : " + bdLocation.getSatelliteNumber());//定位卫星数量
                sBuilder.append("\nHeight : " + bdLocation.getAltitude());  //位置高度
                sBuilder.append("\nDirection : " + bdLocation.getDirection());  //定位方向
                sBuilder.append("\nAddrStr : " + bdLocation.getAddrStr());  //位置详细信息
                sBuilder.append("\nStreet : " + bdLocation.getStreetNumber() + " " + bdLocation.getStreet());//街道号、路名
                sBuilder.append("\nDescribtion : GPS 定位成功");
            }else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                //网络定位结果
                sBuilder.append("\nAddrStr : " + bdLocation.getAddrStr()); //位置详细信息
                sBuilder.append("\nStreet : " + bdLocation.getStreetNumber() + " " + bdLocation.getStreet());//街道号、路名
                sBuilder.append("\nOperators : " + bdLocation.getOperators());//运营商编号
                sBuilder.append("\nDescribtion : 网络定位成功");
            }else if(bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
                //离线定位结果
                sBuilder.append("\nAddrStr : " + bdLocation.getAddrStr()); //位置详细信息
                sBuilder.append("\nStreet : " + bdLocation.getStreetNumber() + " " + bdLocation.getStreet());//街道号、路名
                sBuilder.append("\nDescribtion : 离线定位成功");
            }else if(bdLocation.getLocType() == BDLocation.TypeServerError) {
                sBuilder.append("\nDescribtion : 服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                sBuilder.append("\nDescribtion : 网络故障，请检查网络连接是否正常");
            }else if(bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                sBuilder.append("\nDescribtion : 无法定位结果，一般由于定位SDK内部检测到没有有效的定位依据，" +
                        "比如在飞行模式下就会返回该定位类型， 一般关闭飞行模式或者打开wifi就可以再次定位成功");
            }
            //位置语义化描述
            sBuilder.append("\nLocation decribe : " + bdLocation.getLocationDescribe());
            List<Poi> poiList = bdLocation.getPoiList();  //poi信息（就是附近的一些建筑信息）,只有设置可获取poi才有值
            if(poiList != null) {
                sBuilder.append("\nPoilist size : " + poiList.size());
                for (Poi p : poiList) {
                    sBuilder.append("\nPoi : " + p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.d(TAG, "定位结果详细信息 : " + sBuilder.toString());    //打印以上信息

            //构建生成定位数据对象            //定位数据构建器
            MyLocationData myLocationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())   //设置定位数据的精度信息，单位米
                    .direction(100)                     //设定定位数据的方向信息
                    .latitude(bdLocation.getLatitude()+0.00374531687912) //设定定位数据的纬度
                    .longitude(bdLocation.getLongitude()+0.004474687519)//设定定位数据的经度
                    .build();   //构建生生定位数据对象
            baiduMap.setMyLocationData(myLocationData);  //设置定位数据, 只有先允许定位图层后设置数据才会生效,setMyLocationEnabled(boolean)
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setLatitude(bdLocation.getLatitude()+0.00374531687912 + "");
            locationInfo.setLongitude(bdLocation.getLongitude()+0.004474687519 + "");
            dbManager.addGpsInfo(locationInfo);
            UserAction ua = new UserAction(MainTabActivity.this);
            try{
                SharedPreferences preferences = MainTabActivity.this.getSharedPreferences("UserInformation",MODE_PRIVATE);
                ArrayList<LocationInfo> locationInfos = new ArrayList<LocationInfo>();
                String username = preferences.getString("UserAccount",null);
                DecimalFormat mFormat = new DecimalFormat("00");
                Calendar c = Calendar.getInstance();
                String year = String.valueOf(c.get(Calendar.YEAR));
                String month = mFormat.format(c.get(Calendar.MONTH)+1);
                String date = mFormat.format(c.get(Calendar.DATE));
                String hour = mFormat.format(c.get(Calendar.HOUR_OF_DAY));
                String minute = mFormat.format(c.get(Calendar.MINUTE));

                ua.uploadLocation("LI", username,String.valueOf(bdLocation.getLongitude()),String.valueOf(bdLocation.getLatitude()),
                        year + month + date+ hour + minute, Config.ACTION_UPLOAD,
                        new UserAction.SuccessCallback() {
                    @Override
                    public void onSuccess(String jsonResult) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(jsonResult.toString());
                            LocationStatus = obj.getString("result");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        if (LocationStatus.equals("warn")) {
                            LocationStatus = null;
                            vibrator.vibrate(5000);
                            mDialog = new AlertDialog.Builder(MainTabActivity.this, R.style.AlertDialog_yh)
                                    .setTitle("请注意")
                                    .setMessage("现检测到您已离开指定范围")
                                    .setCancelable(true)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            vibrator.cancel();
                                        }
                                    })
                                    .create();
                            mDialog.show();
                        }
                    }
                },new UserAction.FailCallback() {

                    @Override
                    public void onFail(int status, int reason) {
                        Toast toast = Toast.makeText(MainTabActivity.this,"上传失败",Toast.LENGTH_SHORT);
                    }
                });
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(isFirstLoc) {
                //地理坐标基本数据结构：经度和纬度
                LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                resetOverlay(latLng);
                //描述地图状态将要发生的变化         //生成地图状态将要发生的变化,newLatLngZoom设置地图新中心点
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng,18.0f);
                baiduMap.animateMapStatus(mapStatusUpdate);  //以动画方式更新地图状态，动画耗时 300 ms  (聚焦到当前位置)
            }
        }
    };

    private void requestLocation() {
        if(locationClient != null && locationClient.isStarted()) {
            Log.d(TAG, "requestLocation.");
            locationClient.requestLocation();
        }else if (locationClient != null && !locationClient.isStarted()) {
            Log.d(TAG, "locationClient is started : " + locationClient.isStarted());
            locationClient.start();
        }else {
            Log.e(TAG,"request location error!!!");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        locationClient.unRegisterLocationListener(myListener);
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        clearOverlay();
        bd.recycle();
        mMapView.onDestroy();
        mMapView = null;
        dbManager=null;
    }

    @Override
    protected void onPause(){
        super.onPause();
        mMapView.onPause();
    }

    private void setLocationOption() {
        //获取配置参数对象，用于配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //可选，默认false,设置是否使用gps
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);  //设置定位模式为高精度
        option.setCoorType("bd09ll"); //可选，默认gcj02，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setNeedDeviceDirect(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        option.setScanSpan(10000);
        locationClient.setLocOption(option);
    }

    private void initOverlay(LatLng latLng) {
        Log.d(TAG, "Start initOverlay");

        //设置覆盖物添加的方式与效果
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)//mark出现的位置
                .icon(bd)       //mark图标
                .draggable(true)//mark可拖拽
                .animateType(MarkerOptions.MarkerAnimateType.none);
        marker = (Marker) (baiduMap.addOverlay(markerOptions));//地图上添加mark
        Log.d(TAG,"End initOverlay");
    }
    //清除覆盖物
    private void clearOverlay(){
        baiduMap.clear();
        marker = null;
    }
    //重置覆盖物
    private void resetOverlay(LatLng latLng) {
        clearOverlay();
        initOverlay(latLng);
    }


    //检测到摔倒，用户判断时间
    public void Alert() {
        initDialog();
        player = MediaPlayer.create(MainTabActivity.this,R.raw.alarm);
        player.setLooping(true);
        player.start();
    }

    public void initDialog() {
        mOffTextView = new TextView(this);
        mOffTextView.setTextSize(26);
        mOffTextView.setGravity(Gravity.CENTER);
        mOffTextView.setTextColor(Color.rgb(255,255,255));
        mDialog = new AlertDialog.Builder(this, R.style.AlertDialog_yh)
                .setTitle("警告")
                .setView(mOffTextView)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOffTime.cancel();
                        dialog.cancel();
                       // vibrator.cancel();
                        player.stop();
                    }
                })
                .create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        mOffHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    mOffTextView.setText("检测到摔倒   " + msg.what);
                } else {
                    if (mDialog != null){
                        mDialog.cancel();
                        vibrator.cancel();
                        UserAction ua = new UserAction(MainTabActivity.this);
                        try{
                            SharedPreferences preferences = MainTabActivity.this.getSharedPreferences("UserInformation",MODE_PRIVATE);
                            String username = preferences.getString("UserAccount",null);
                            DecimalFormat mFormat = new DecimalFormat("00");
                            Calendar c = Calendar.getInstance();
                            String year = String.valueOf(c.get(Calendar.YEAR));
                            String month = mFormat.format(c.get(Calendar.MONTH)+1);
                            String date = mFormat.format(c.get(Calendar.DATE));
                            String hour = mFormat.format(c.get(Calendar.HOUR_OF_DAY));
                            String minute = mFormat.format(c.get(Calendar.MINUTE));
                            ua.upload("FD", username, "falling", year + month + date, hour+":"+ minute, Config.ACTION_UPLOAD,
                                    new UserAction.SuccessCallback() {
                                @Override
                                public void onSuccess(String jsonResult) {
                                    Toast toast = Toast.makeText(MainTabActivity.this,"信息上传成功！",Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                                    toast.show();
                                }
                            },new UserAction.FailCallback() {

                                @Override
                                public void onFail(int status, int reason) {
                                    // TODO Auto-generated method stub
                                    new AlertDialog.Builder(MainTabActivity.this)
                                            .setTitle("tips")
                                            .setMessage("上传失败~")
                                            .setPositiveButton("确定", null)
                                            .show();
                                }
                            });
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:18818272458"));
                        startActivity(intent);
                    }
                    mOffTime.cancel();
                }
                super.handleMessage(msg);
            }
        };

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = 10;
            @Override
            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandle.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initArcView() {
        cc = (StepArcView) findViewById(R.id.cc);
        calorie = (TextView)findViewById(R.id.edit_calorie);
        distance = (TextView)findViewById(R.id.edit_distance);
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    int temp = 0;
                    while (true) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (StepCounterService.FLAG) {
                            Message msg = new Message();
                            if (temp != StepDetector.CURRENT_STEP) {
                                temp = StepDetector.CURRENT_STEP;
                            }
                            handler.sendMessage(msg);
                        }
                    }
                }
            };
            thread.start();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //super.handleMessage(msg);
            countStep();
            cc.setCurrentCount(200,total_step);
            double mile = (double)11*total_step/20;
            distance.setText(""+mile+" 米");
        }
    };

    private void countStep(){
        if (StepDetector.CURRENT_STEP % 2 == 0){
            total_step = StepDetector.CURRENT_STEP;
        }else {
            total_step = StepDetector.CURRENT_STEP + 1;
        }
        total_step = StepDetector.CURRENT_STEP;
        if (total_step > 200){
            stopService(new Intent(this, StepCounterService.class));
            handler.removeCallbacks(thread);
        }
    }

    @SuppressLint("NewApi")//作用是屏蔽android lint错误
    private void initActionBar() {
        actionBar = super.getActionBar();//获得actionbar
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);//显示home区域
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);//显示返回图片
        actionBar.setHomeAsUpIndicator(R.drawable.com_btn);//设置返回图标

        //去除默认的ICON图标
        Drawable colorDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        actionBar.setIcon(colorDrawable);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView tvTitle = new TextView(this);
       //tvTitle.setText("主 页");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(18);
        tvTitle.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tvTitle.setLayoutParams(params);
        actionBar.setCustomView(tvTitle);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
    private void initDrawerLayout() {
        information = (TextView) findViewById(R.id.information);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainTabActivity.this, InforSettingActivity.class);
                startActivity(intent);
            }
        });
        alarm = (TextView) findViewById(R.id.alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加入界面跳转代码
            }
        });
        track = (TextView)findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainTabActivity.this, TraceActivity.class);
                startActivity(intent);
            }
        });
        steps = (TextView)findViewById(R.id.steps);
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainTabActivity.this, HistoryStep.class);
                startActivity(intent);
            }
        });
        drawerLayout = (DrawerLayout) super.findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.back_move_details_normal, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(toggle);
    }

    private void toggleLeftSliding() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            toggleLeftSliding();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
}

