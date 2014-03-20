package com.wuxue.NearbyYourSelf;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 14-3-19.
 */
public class JSONAnalyse {
    public JSONAnalyse(){

    }

    /**
     *  poiOverlay.removeAll();

     JSONArray poilistJsonArray = rootJsonObject.optJSONArray("poilist");
     for (int i = 0; i < poilistJsonArray.length(); i++) {
     JSONObject poiJsonObject = poilistJsonArray.optJSONObject(i);

     double longitude = poiJsonObject.optDouble("x");
     double latitude = poiJsonObject.optDouble("y");
     String name = poiJsonObject.optString("name");

     GeoPoint p = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
     PoiItem item = new PoiItem(p, name, "");

     item.setMarker(getResources().getDrawable(R.drawable.pin_restaurant));
     item.setType(PoiItem.TYPE_RESTAURANT);

     poiOverlay.addItem(item);
     */
    public  List<Map<String, Object>>  getJSONObject(){
        FileOutputStream fileOutputStream = null;
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        List<Map<String, Object>> list = null;
        try {
            fileOutputStream = new FileOutputStream(new File("weibo_location_type_.json"));
            try {
                jsonObject = new JSONObject(fileOutputStream.toString());
                jsonArray = jsonObject.optJSONArray("大类");
                JSONObject jsonObject1 = null;
                list = new ArrayList<Map<String, Object>>();
                for(int i = 0;i < jsonArray.length();i ++ ){
                    jsonObject1 = jsonArray.optJSONObject(i);
                    String name = jsonObject1.getString("bigTypeName");
                    Map <String,Object> map = new HashMap<String, Object>();
                    map.put("service",name);
                    list.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
