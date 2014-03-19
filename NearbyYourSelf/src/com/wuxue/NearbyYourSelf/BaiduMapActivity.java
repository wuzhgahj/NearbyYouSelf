package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.baidu.mapapi.map.MapView;

/**
 * Created by wzg on 14-3-18.
 */
public class BaiduMapActivity extends Activity {
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.baiduview);
        mapView = (MapView)findViewById(R.id.mapView);
    }
}
