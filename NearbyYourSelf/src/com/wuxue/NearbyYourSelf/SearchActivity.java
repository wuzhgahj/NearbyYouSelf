package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
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
    private ListView listInfo;
    private ImageButton btSearchs;
    private String text;
    private final String CITY_CODE = "0029";
    private final String TAG = "wawawawa";
    private String resultStr;
    private String name;
    private String telephone;
    private String address;
    private int page = 10;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search);
        init();
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btSearchs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = getEditText();
                if (text == null || text.equals("")) {
                    Toast.makeText(SearchActivity.this, "请输入要搜索的东东", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "进来了");
                    search();
                    adapter = new SimpleAdapter
                            (SearchActivity.this, list, R.layout.listinfo,
                                    new String[]{"name", "address", "distance"},
                                    new int[]{R.id.textInfo1, R.id.textInfo2, R.id.textInfo3});
                    listInfo.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //初始化各种....
    public void init() {
        btBack = (ImageButton) findViewById(R.id.btBack);
        editText = (EditText) findViewById(R.id.editText);
        listInfo = (ListView) findViewById(R.id.listInfo);
        btSearchs = (ImageButton) findViewById(R.id.btSearchs);
        nowLatitude = Double.valueOf(getIntent().getStringExtra("nowLatitude"));
        nowLongitude = Double.valueOf(getIntent().getStringExtra("nowLongitude"));
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
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage().toString());
        }
        final String url = "https://api.weibo.com/2/location/pois/search/by_geo.json?city=" + CITY_CODE + "&" + "q=" + text + "&" + "access_token=2.003bqBkC0rLoqt8775fb7bffR7utyC";
        Log.d(TAG, url);
        final HttpGet request = new HttpGet(url);
        int timeout = 30000;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        final DefaultHttpClient client = new DefaultHttpClient();
        client.setParams(params);
        AsyncTask<Integer, Integer, Integer> task = new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
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
                return 0;
            }


            @Override
            protected void onPostExecute(Integer integer) {
                progressDialog.dismiss();
                Toast.makeText(SearchActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject rootJsonObject = new JSONObject(resultStr);
                    Log.d("wawawawawa", rootJsonObject.toString());
                    JSONArray poilistJsonArray = rootJsonObject.optJSONArray("poilist");
                    for (int i = 0; i < poilistJsonArray.length(); i++) {
                        JSONObject poiJsonObject = (JSONObject) poilistJsonArray.get(i);
                        name = (String) poiJsonObject.get("name");
                        address = (String) poiJsonObject.get("address");
                        telephone = (String) poiJsonObject.get("tel");
                        shopLongitude = Double.parseDouble((String)poiJsonObject.get("x"));
                        shopLatitude = Double.parseDouble((String)poiJsonObject.get("y"));
                        distance = GetShortDistance(shopLatitude, shopLongitude, nowLatitude, nowLongitude);

                        distance = distance / 1000;

                        Log.d(TAG, name + address + telephone);
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("name", name);
                        map.put("address", address);
                        map.put("distance", (int)distance + "km");
                        list.add(map);
                    }
                } catch (JSONException e) {
                    Log.e(TAG + "error", e.getMessage().toString());
                }
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(SearchActivity.this);
                progressDialog.setMessage("加载中...");
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

                super.onProgressUpdate(values);
            }
        };
        task.execute(0);
    }

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
