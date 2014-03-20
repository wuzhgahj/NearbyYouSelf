package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import java.util.ArrayList;

/**
 * Created by wzg on 14-3-18.
 */
public class BaiduMapActivity extends Activity{
    private MapView mapView;
    private MapController controller;
    private Double nowLongitude;
    private Double nowLatitude;
    private final String TAG = "wawawawa";
    private Double shopLatitude;
    private Double shopLongitude;
    private LocationClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.baiduview);
        Log.d(TAG, getIntent().getStringExtra("nowLatitude") + "");
        //获取自己所在的位置
        nowLatitude = Double.parseDouble(getIntent().getStringExtra("nowLatitude"));
        nowLongitude = Double.parseDouble(getIntent().getStringExtra("nowLongitude"));
        //获取已选择商户的位置
        shopLatitude = Double.parseDouble(getIntent().getStringExtra("shopLatitude"));
        shopLongitude = Double.parseDouble(getIntent().getStringExtra("shopLongitude"));
        mapView = (MapView)findViewById(R.id.mapView);
        //设置起始位置和结束位置
        GeoPoint start = new GeoPoint((int)(nowLatitude*1E6),(int)(nowLongitude*1E6));
        GeoPoint end = new GeoPoint((int)(shopLatitude*1E6),(int)(shopLongitude*1E6));
        controller = mapView.getController();
        controller.setCenter(end);
        controller.setZoom(12);
        //添加覆盖物涂层
        OverlayItem item1 = new OverlayItem(start,"item1","item1");
        OverlayItem item2 = new OverlayItem(end,"item2","item2");
        Drawable mark1 = getResources().getDrawable(R.drawable.ic_loc_from);
        Drawable mark2 = getResources().getDrawable(R.drawable.ic_loc_to);
        item2.setMarker(mark2);
        MyPoiOverlay myPoiOverlay = new MyPoiOverlay(mark1,mapView);
        mapView.getOverlays().add(myPoiOverlay);
        myPoiOverlay.addItem(item1);
        myPoiOverlay.addItem(item2);

        walk();
        mapView.refresh();
    }

    public void walk(){

    }


    public void doReceviceLocation(BDLocation bdLocation) {

    }

    public void localMe() {
        if (!client.isStarted()) {
            client.start();
        }
        client.requestLocation();
    }

    @Override
    protected void onDestroy() {
        mapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }



    class MyPoiOverlay extends ItemizedOverlay{

        public MyPoiOverlay(Drawable drawable, MapView mapView) {
            super(drawable, mapView);
        }

        protected boolean onTap(int index) {
            //在此处理item点击事件
            System.out.println("item onTap: "+index);
            return true;
        }
        public boolean onTap(GeoPoint pt, MapView mapView){
            //在此处理MapView的点击事件，当返回 true时
            super.onTap(pt,mapView);
            return false;
        }
    }
}
