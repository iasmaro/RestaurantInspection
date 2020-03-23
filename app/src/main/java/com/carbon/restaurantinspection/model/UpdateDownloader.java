package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UpdateDownloader {
    private static final String LAST_DOWNLOAD = "LastDownload";
    private static final int MILLISECS_TO_HOURS = 3600000;
    private static final BigInteger DEFAULT_DATE = new BigInteger("1584687607039");
    private static final String RESTAURANTS_URL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String INSPECTIONS_URL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private String restaurantsJson;
    private boolean checkForUpdates(Context context) {
        Date date = new Date();
        SharedPreferences prefs = context.getSharedPreferences(LAST_DOWNLOAD, Context.MODE_PRIVATE);
        long savedDate = prefs.getLong(LAST_DOWNLOAD, DEFAULT_DATE.longValue());
        long hours = (date.getTime() - savedDate)/MILLISECS_TO_HOURS;
        return hours >= 20;
    }
    public boolean updatesAvailable(Context context) {
        boolean updatesAvailable = false;
        if (checkForUpdates(context)) {
            getRestaurant(context, RESTAURANTS_URL);
            getRestaurant(context, INSPECTIONS_URL);
            Log.d("update", "" + restaurantsJson);
            // check city of surrey website for updates
            // return if date is less than saved date
            updatesAvailable = true;
        }
        return updatesAvailable;
    }

    private void getRestaurant(Context context, String url) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    restaurantsJson = response.getString("result");
                    JSONObject result = new JSONObject(restaurantsJson);
                    JSONArray ja = result.getJSONArray("resources");
                    String date = (ja.getJSONObject(0).getString("last_modified"));
                    String csvURL = (ja.getJSONObject(0).getString("url"));
                    String otherDate = date.substring(0, 10) + " " + date.substring(11);
                    Date thisDate = getDate(otherDate);
                    Log.d("update", "last_modified: " + thisDate.toString());
                    Log.d("update", "last_modified: " + csvURL);
                } catch (JSONException e) {
                    restaurantsJson = "something went wrong";
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("UpdateDownloader", "Error: "+ error);
            }
        });

        queue.add(jsonObjectRequest);
    }
    private Date getDate(String someDate){
        Date inspecDate;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            inspecDate = dateFormat.parse(someDate);
            return inspecDate;
        } catch (ParseException e) {
            return inspecDate = new Date();
        }
    }

    public void downloadUpdates(Context context) {
        // Download updates from city of surrey website
        // allow user to cancel if necessary
        // once download is complete, remove current data set
        // rename newly downloaded data set
        Date date = new Date();
        SharedPreferences prefs = context.getSharedPreferences(LAST_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(LAST_DOWNLOAD, date.getTime());
    }

    public int downloadProgress() {
        // report download progress to user
        return 0;
    }

    public void cancelUpdate() {
        // cancel updates
    }
}
