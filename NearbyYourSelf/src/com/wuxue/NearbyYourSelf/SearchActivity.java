package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by wzg on 14-3-7.
 */
public class SearchActivity extends Activity {
    private ImageButton btBack;
    private EditText editText;
    private PullToRefreshListView listInfo;
    private ImageButton btSearchs;
    private String text;
    private final String CITY_CODE = "0029";
    private final String TAG = "wawawawa";
    private String resultStr;
    private String name;
    private String telephone;
    private String address;
    private int page = 5;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private static double DEF_PI = 3.14159265359; // PI
    private static double DEF_2PI = 6.28318530712; // 2*PI
    private static double DEF_PI180 = 0.01745329252; // PI/180.0
    private static double DEF_R = 6370693.5; // radius of earth
    private double nowLongitude;
    private double nowLatitude;
    private double shopLongitude;
    private double shopLatitude;
    private ProgressDialog progressDialog;
    private double distance;
    private int a = 0;//这个变量a 本人认为非常之叼,负责不重复显示
    private String old;//这个old也非常叼,主要判断是否查询同样的参数
    private HttpGet request = null;
    private final DefaultHttpClient client = new DefaultHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search);
        init();
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MyActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btSearchs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = getEditText();
                //判断是否为空
                if (text == null || text.equals("")) {
                    Toast.makeText(SearchActivity.this, "请输入要搜索的东东", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "进来了");
                    //向http发送请求,并且给ArrayList<Map<String,Object>> list 初始化
                    search();
                    adapter = new SimpleAdapter
                            (SearchActivity.this, list, R.layout.listinfo,
                                    new String[]{"name", "address", "distance"},
                                    new int[]{R.id.textInfo1, R.id.textInfo2, R.id.textInfo3});
                    listInfo.setAdapter(adapter);
                    old = text;
                }
            }
        });

        //加刷新监听事件
        listInfo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });
        //添加点击事件
        listInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = list.get(position);
                Intent intent = new Intent(SearchActivity.this, BaiduMapActivity.class);
                intent.putExtra("shopLatitude", map.get("shopLatitude").toString());
                intent.putExtra("shopLongitude", map.get("shopLongitude").toString());
                intent.putExtra("nowLongitude", nowLongitude);
                intent.putExtra("nowLatitude", nowLatitude);
                Log.d(TAG, shopLatitude + " 商店 " + shopLongitude + " 自己 " + nowLatitude + nowLongitude);
                startActivity(intent);
            }
        });
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            //控制下拉刷新显示的条数
            page += 5;
            refreshSearch();
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call onRefreshComplete when the list has been refreshed.
            //完成后解析json,并且adapter.notifyDataChanged();
            refreshComplete();
            listInfo.onRefreshComplete();
            super.onPostExecute(result);
        }
    }


    //初始化各种....
    public void init() {
        btBack = (ImageButton) findViewById(R.id.btBack);
        editText = (EditText) findViewById(R.id.editText);
        listInfo = (PullToRefreshListView) findViewById(R.id.listInfo);
        btSearchs = (ImageButton) findViewById(R.id.btSearchs);
        nowLatitude = Double.valueOf(getIntent().getStringExtra("nowLatitude"));
        nowLongitude = Double.valueOf(getIntent().getStringExtra("nowLongitude"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //获取editText的数据
    public String getEditText() {
        text = editText.getText().toString();
        if (text != null && !text.equals("")) {
            return text;
        } else
            return null;
    }

    //查找要查的东西
    public void search() {
        page = 5;
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage().toString());
        }
        if (!text.equals(old)) {
            list.clear();
        } else {
            return;
        }
        final String url = "https://api.weibo.com/2/location/pois/search/by_geo.json?city=" + CITY_CODE + "&" + "q=" + text + "&" + "access_token=2.003bqBkC0rLoqt8775fb7bffR7utyC" + "&" + "count=" + page;
        Log.d(TAG, url);
        request = new HttpGet(url);
        int timeout = 30000;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, timeout);

        client.setParams(params);
        AsyncTask<Integer, Integer, Integer> task = new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                requestHttp(request);
                return 0;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                progressDialog.dismiss();
                Toast.makeText(SearchActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                jsonbt();
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(SearchActivity.this);
                progressDialog.setMessage("加载中...");
                progressDialog.show();
                super.onPreExecute();
            }

        };
        task.execute(0);
    }

    //这是下拉事件调用的解析json的方法
    public void json() {
        try {
            JSONObject rootJsonObject = new JSONObject(resultStr);
            Log.d(TAG, rootJsonObject.toString());
            JSONArray poilistJsonArray = rootJsonObject.optJSONArray("poilist");
            if (poilistJsonArray == null) {
                Toast.makeText(this, "查不到结果哦", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = a; i < poilistJsonArray.length(); i++) {
                JSONObject poiJsonObject = (JSONObject) poilistJsonArray.get(i);
                name = (String) poiJsonObject.get("name");
                address = (String) poiJsonObject.get("address");
                telephone = (String) poiJsonObject.get("tel");
                shopLongitude = Double.parseDouble((String) poiJsonObject.get("x"));
                shopLatitude = Double.parseDouble((String) poiJsonObject.get("y"));
                distance = GetShortDistance(shopLatitude, shopLongitude, nowLatitude, nowLongitude);
                distance = distance / 1000;
                Log.d(TAG, name + address + telephone);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", name);
                map.put("address", address);
                map.put("distance", (int) distance + "km");
                map.put("shopLongitude", shopLongitude);
                map.put("shopLatitude", shopLatitude);
                map.put("nowLongitude", nowLongitude);
                map.put("nowLatitude", nowLatitude);
                list.add(map);
            }
            a += 5;
        } catch (JSONException e) {
            Log.e(TAG + "error", e.getMessage().toString());
        }
    }

    //这是搜索的点击事件里调用的解析json
    public void jsonbt() {
        a = 0;
        adapter.notifyDataSetChanged();
        try {
            JSONObject rootJsonObject = new JSONObject(resultStr);
            Log.d(TAG, rootJsonObject.toString());
            JSONArray poilistJsonArray = rootJsonObject.optJSONArray("poilist");
            if (poilistJsonArray == null) {
                Toast.makeText(this, "查不到结果哦", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = a; i < poilistJsonArray.length(); i++) {
                JSONObject poiJsonObject = (JSONObject) poilistJsonArray.get(i);
                name = (String) poiJsonObject.get("name");
                address = (String) poiJsonObject.get("address");
                telephone = (String) poiJsonObject.get("tel");
                shopLongitude = Double.parseDouble((String) poiJsonObject.get("x"));
                shopLatitude = Double.parseDouble((String) poiJsonObject.get("y"));
                distance = GetShortDistance(shopLatitude, shopLongitude, nowLatitude, nowLongitude);
                distance = distance / 1000;
                Log.d(TAG, name + address + telephone);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", name);
                map.put("address", address);
                map.put("distance", (int) distance + "km");
                map.put("shopLongitude", shopLongitude);
                map.put("shopLatitude", shopLatitude);
                map.put("nowLongitude", nowLongitude);
                map.put("nowLatitude", nowLatitude);
                list.add(map);
            }
            a += 5;
        } catch (JSONException e) {
            Log.e(TAG + "error", e.getMessage().toString());
        }
    }

    //向服务器发送请求
    public void requestHttp(HttpGet request) {
        try {
            HttpResponse response = client.execute(request);
            InputStream inputStream = response.getEntity().getContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int length;
            while ((length = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, length);
            }
            outputStream.close();
            inputStream.close();
            resultStr = outputStream.toString("UTF-8");
            Log.d(TAG, resultStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //下拉刷新的请求
    public void refreshSearch() {
        final String url = "https://api.weibo.com/2/location/pois/search/by_geo.json?city=" + CITY_CODE + "&" + "q=" + text + "&" + "access_token=2.003bqBkC0rLoqt8775fb7bffR7utyC" + "&" + "count=" + page;
        Log.d(TAG, url);
        final HttpGet request = new HttpGet(url);
        int timeout = 30000;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        final DefaultHttpClient client = new DefaultHttpClient();
        client.setParams(params);
        requestHttp(request);
    }

    public void refreshComplete() {
        json();
    }

    //获取两点之间的距离
    public double GetShortDistance(double lon1, double lat1, double lon2, double lat2) {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI)
            dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
            dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }
}
