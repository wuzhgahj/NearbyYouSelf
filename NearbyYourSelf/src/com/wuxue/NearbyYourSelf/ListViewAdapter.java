package com.wuxue.NearbyYourSelf;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.wuxue.NearbyYourSelf.R;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class ListViewAdapter extends SimpleAdapter {
    Context context = null;

    public ListViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final View view = super.getView(position, convertView, parent);
        ImageButton btnNext = (ImageButton) view.findViewById(R.id.btNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "点击第" + pos + "按钮");
                Intent intent = new Intent(context, ItemsActivity.class);

                view.getContext().startActivity(intent);
            }
        });
        final TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setSelected(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "click" + pos + "textView" + textView.getText().toString());
            }
        });
        return view;
    }
}