package com.wuxue.NearbyYourSelf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * Created by wzg on 14-3-7.
 */
public class SearchActivity extends Activity {
    private ImageButton btBack;
    private EditText editText;
    private ListView listInfo;
    private ImageButton btSearchs;

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
    }

    public void init() {
        btBack = (ImageButton) findViewById(R.id.btBack);
        editText = (EditText) findViewById(R.id.editText);
        listInfo = (ListView) findViewById(R.id.listInfo);
        btSearchs = (ImageButton) findViewById(R.id.btSearchs);
    }
}
