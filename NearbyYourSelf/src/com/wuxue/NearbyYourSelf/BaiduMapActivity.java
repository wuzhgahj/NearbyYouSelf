package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * Created by wzg on 14-3-18.
 */
public class BaiduMapActivity extends Activity {
    private MapView mapView;
    private MapController controller;
    private Double nowLongitude;
    private Double nowLatitude;
    private final String TAG = "wawawawa";
    private Double shopLatitude;
    private Double shopLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.baiduview);
        Log.d(TAG,getIntent().getStringExtra("nowLatitude")+"");
        nowLatitude = Double.parseDouble(getIntent().getStringExtra("nowLatitude"));
        nowLongitude = Double.parseDouble(getIntent().getStringExtra("nowLongitude"));
        shopLatitude = Double.parseDouble(getIntent().getStringExtra("shopLatitude"));
        shopLongitude = Double.parseDouble(getIntent().getStringExtra("shopLongitude"));
        mapView = (MapView)findViewById(R.id.mapView);
        GeoPoint point = new GeoPoint((int)(shopLatitude*1E6),(int)(shopLongitude*1E6));
        controller = mapView.getController();
        controller.setCenter(point);
        controller.setZoom(12);
    }
}
