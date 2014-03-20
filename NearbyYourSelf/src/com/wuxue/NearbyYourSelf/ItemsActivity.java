package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 14-3-19.
 */
public class ItemsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items);
        ListView listView = (ListView) findViewById(R.id.listView);
        List<Map<String, Object>> list = new JSONAnalyse().getJSONObject();

        final ListViewAdapter adapter = new ListViewAdapter(
                this, list, R.layout.listview, new String[]{"servers"},
                new int[]{R.id.textView});
        listView.setAdapter(adapter);
    }
}
