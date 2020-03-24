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
import java.util.Date;

public class UpdateDownloader {
    private static final String LAST_DOWNLOAD = "LastDownload";
    private static final int MILLISECS_TO_HOURS = 3600000;
    private static final BigInteger DEFAULT_DATE = new BigInteger("1574686607039");
    private static final String RESTAURANTS_URL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String INSPECTIONS_URL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private JSONObject restaurantsJson;
    private JSONObject inspectionsJson;
    private String restaurantsDownloadURL;
    private String inspectionsDownloadURL;
    private boolean restaurantsUpdateAvailable;
    private boolean inspectionsUpdateAvailable;
    private long lastUpdate;
    private boolean restaurantsReady = false;
    private boolean inspectionsReady = false;

    public UpdateDownloader(Context context) {
        getRestaurant(context, RESTAURANTS_URL);
        getRestaurant(context, INSPECTIONS_URL);
    }

    private boolean checkForUpdates(Context context) {
        Date date = new Date();
        SharedPreferences prefs = context.getSharedPreferences(LAST_DOWNLOAD, Context.MODE_PRIVATE);
        lastUpdate = prefs.getLong(LAST_DOWNLOAD, DEFAULT_DATE.longValue());
        long hours = (date.getTime() - lastUpdate)/MILLISECS_TO_HOURS;
        return hours >= 20;
    }
    public boolean updatesAvailable(Context context) {
        boolean updatesAvailable = false;
        if (checkForUpdates(context) && restaurantsReady && inspectionsReady) {
            try {
                Date restaurantsUpdate = getDate(restaurantsJson.getString("last_modified"));
                restaurantsDownloadURL = restaurantsJson.getString("url");
                Date inspectionsUpdate = getDate(inspectionsJson.getString("last_modified"));
                inspectionsDownloadURL = inspectionsJson.getString("url");
                Log.d("update", restaurantsUpdate.toString());
                Log.d("update", restaurantsDownloadURL);
                Log.d("update", inspectionsUpdate.toString());
                Log.d("update", inspectionsDownloadURL);
                restaurantsUpdateAvailable = restaurantsUpdate.getTime() > lastUpdate;
                inspectionsUpdateAvailable = inspectionsUpdate.getTime() > lastUpdate;
                updatesAvailable = restaurantsUpdateAvailable || inspectionsUpdateAvailable;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return updatesAvailable;
    }

    private void getRestaurant(Context context, final String url) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (url == RESTAURANTS_URL) {
                        restaurantsJson = response.getJSONObject("result")
                                .getJSONArray("resources").getJSONObject(0);
                        restaurantsReady = true;
                    } else if (url == INSPECTIONS_URL) {
                        inspectionsJson = response.getJSONObject("result")
                                .getJSONArray("resources").getJSONObject(0);
                        inspectionsReady = true;
                    }
                } catch (JSONException e) {
                    Log.wtf("UpdateDownloader", "Error: "+ e);
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
        String correctDate = someDate.substring(0,10) + " " + someDate.substring(11);
        Date inspecDate;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            inspecDate = dateFormat.parse(correctDate);
            return inspecDate;
        } catch (ParseException e) {
            return inspecDate = new Date();
        }
    }

    public void downloadUpdates(Context context) {
        if (inspectionsUpdateAvailable) {
            downloadInspectionUpdates(context);
        }
        if (restaurantsUpdateAvailable) {
            downloadRestaurantUpdates(context);
        }
    }

    public void downloadRestaurantUpdates(Context context) {
        // Download updates from city of surrey website
        // allow user to cancel if necessary
        // once download is complete, remove current data set
        // rename newly downloaded data set
        Date date = new Date();
        SharedPreferences prefs = context.getSharedPreferences(LAST_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(LAST_DOWNLOAD, date.getTime());
    }

    public void downloadInspectionUpdates(Context context) {
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

    public boolean isReady() {
        return restaurantsReady && inspectionsReady;
    }
}
