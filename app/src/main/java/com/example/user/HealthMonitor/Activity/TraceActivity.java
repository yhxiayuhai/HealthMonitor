package com.example.user.HealthMonitor.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.user.HealthMonitor.DbHelper.DBManager;
import com.example.user.HealthMonitor.R;
import com.example.user.HealthMonitor.Trace.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class TraceActivity extends Activity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Boolean D = true;
    private List<LatLng> resultPoints;
    List<LocationInfo> infos;
    private Marker mMoveMarker;
    private Polyline mVirtureRoad;

    public TraceActivity() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) {
            Log.i("GPS轨迹", "onCreate()");
        }
        setContentView(R.layout.activity_trace);
        super.onCreate(savedInstanceState);
        //初始化地图相关
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

    }

    /**
     * 显示轨迹
     */
    private void showTrack(){
        if(D){
            Log.i("GPS轨迹","showTrack()");
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                resultPoints=new ArrayList<LatLng>();
                //获取数据库的数据
                DBManager dbManager =new DBManager(getApplicationContext());
                infos=dbManager.findAll();
                for(LocationInfo info:infos){
                    Double myLatitude=Float.parseFloat(info.getLatitude()) - 0.00374531687912 ;
                    Double 	myLongitude=Float.parseFloat(info.getLongitude())- 0.004474687519;
                    LatLng point=new LatLng(myLatitude, myLongitude);
                    resultPoints.add(point);
                    point=null;

                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                LatLng cenpt = new LatLng(Float.parseFloat(infos.get(infos.size()-1).getLatitude())-0.00374531687912,
                        Float.parseFloat(infos.get(infos.size() - 1).getLongitude())- 0.004474687519);
                MapStatus mapStatus = new MapStatus.Builder()
                        .target(cenpt)
                        .zoom(18)
                        .build();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                mBaiduMap.setMapStatus(mapStatusUpdate);
                //数据库最后一个坐标的位置
               /* MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(Float.parseFloat(infos.get(infos.size()-1).getLatitude())-0.00374531687912,
                        Float.parseFloat(infos.get(infos.size() - 1).getLongitude())- 0.004474687519));
                mBaiduMap.setMapStatus(u);*/
                //折线显示
                OverlayOptions ooPolyline = new PolylineOptions().width(10)
                        .color(0xAAFF0000).points(resultPoints);
                mVirtureRoad = (Polyline) mBaiduMap.addOverlay(ooPolyline);
               // mBaiduMap.addOverlay(ooPolyline);
               // OverlayOptions markerOptions;
            //    markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory
            //            .fromResource(R.drawable.marker)).position(resultPoints.get(0)).rotate((float) getAngle(0));
             //   mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
                super.onPostExecute(result);
            }

        }.execute();


    }

    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
        LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 算取斜率
     */
    private double getSlope(int startIndex) {
        if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
        LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
        return getSlope(startPoint, endPoint);
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }
    @Override
    protected void onPause() {
        if(D){
            Log.i("GPS轨迹","onPause()");
        }
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(D){
            Log.i("GPS轨迹","onResume()");
        }
        mMapView.onResume();
        showTrack();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(D){
            Log.i("GPS轨迹","onDestroy()");
        }
        mMapView.onDestroy();
        super.onDestroy();
    }
}






