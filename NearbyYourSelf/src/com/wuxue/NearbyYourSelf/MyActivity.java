package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private ImageButton btSearch;
    private ImageButton btSetting;
    private LinearLayout layout;
    private ImageButton btLocation;
    private TextView addressText;
    private ListView listView;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private LocationClient locationClient = null;
    private final String TAG = "wawawawa";
    private String nowLongitude = null;
    private String nowLatitude = null;
    private Intent intent = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        init();
        final ListViewAdapter adapter = new ListViewAdapter(
                this, list, R.layout.listview, new String[]{"servers"},
                new int[]{R.id.textView});
        listView.setAdapter(adapter);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((String)nowLongitude)==null||((String)nowLatitude)==null||intent==null){
                    Log.d(TAG,"double是空的啊");
                    Toast.makeText(MyActivity.this,"定位中,请稍等...",Toast.LENGTH_SHORT).show();
                }else{
                    search();
                    startActivity(intent);
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position", "" + position);
                adapter.notifyDataSetChanged();
            }
        });


        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressText.setText("正在获取地理信息,请稍后...");
                location();
            }
        });

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(0);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向

        locationClient = new LocationClient(getApplicationContext());

        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation == null) {
                    Log.d(TAG, "onReceiveLocation return null");
                    return;
                }
                addressText.setText(bdLocation.getAddrStr());
                //获取现在所处的经纬度
                nowLatitude = bdLocation.getLatitude()+"";
                nowLongitude = bdLocation.getLongitude()+"";
                //向SearchActivity传递经纬度
                intent = new Intent(MyActivity.this,SearchActivity.class);
                intent.putExtra("nowLatitude",nowLatitude);
                intent.putExtra("nowLongitude",nowLongitude);
            }

            @Override
            public void onReceivePoi(BDLocation bdLocation) {
                Log.d(TAG, "onReceivePoi");
            }
        });
        locationClient.setLocOption(option);
        addressText.setText("正在获取地理信息,请稍后...");
        location();
    }


    public void init() {
        btSearch = (ImageButton) findViewById(R.id.btSearch);
        btSetting = (ImageButton) findViewById(R.id.btnSetting);
        btLocation = (ImageButton) findViewById(R.id.btLocation);
        addressText = (TextView) findViewById(R.id.addressText);
        listView = (ListView) findViewById(R.id.listView);//下拉刷新
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("servers", "餐饮服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "购物服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "生活服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "体育休闲服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "医疗保健服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "住宿服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "科教文化服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "交通设施服务");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("servers", "公共设施");
        list.add(map);
    }

    public void search() {
        if(addressText==null||addressText.equals("")){
            Toast.makeText(this,"定位中,请稍等....",Toast.LENGTH_SHORT).show();
        }else {
            startActivity(intent);
        }

    }

    private void location() {
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        locationClient.requestLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}