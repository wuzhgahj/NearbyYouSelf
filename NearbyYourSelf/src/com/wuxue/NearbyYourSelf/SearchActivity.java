package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


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
    private final String TAG = "Nearby";
    private String resultStr;
    private String city_name;
    private String province_name;
    private String name;
    private String telephone;
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
        final String url = "https://api.weibo.com/2/location/pois/search/by_location.json?city="+CITY_CODE+"&"+"q="+text +"&"+"access_token=2.003bqBkC0rLoqt8775fb7bffR7utyC";
        Log.d(TAG,url);
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
                Toast.makeText(SearchActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(resultStr);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("pois");
                    resultStr = jsonArray.getString(0);
                    jsonObject = new JSONObject(resultStr);
                    resultStr = (String) jsonObject.get("category");
                    Log.d(TAG+"Result",resultStr);
                } catch (JSONException e) {
                    Log.e(TAG+"error", e.getMessage().toString());
                }
            }
        };
        task.execute(0);
    }
}
