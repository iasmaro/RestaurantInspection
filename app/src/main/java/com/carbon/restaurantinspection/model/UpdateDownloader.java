package com.carbon.restaurantinspection.model;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carbon.restaurantinspection.service.FileDownloadClient;
import com.carbon.restaurantinspection.ui.MapActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.app.ActivityCompat;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

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
    private boolean restaurantDownloadComplete = false;
    private boolean inspectionDownloadComplete = false;

    public UpdateDownloader(Context context) {
        getRestaurant(context, RESTAURANTS_URL);
        getRestaurant(context, INSPECTIONS_URL);
    }

    private boolean checkForUpdates(Context context) {
        Date date = new Date();
        SharedPreferences prefs = context.getSharedPreferences(LAST_DOWNLOAD, Context.MODE_PRIVATE);
        lastUpdate = prefs.getLong(LAST_DOWNLOAD, DEFAULT_DATE.longValue());
        Log.d("update", "" + lastUpdate);
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
            downloadNewUpdates(context, inspectionsDownloadURL);
        } else {
            inspectionDownloadComplete = true;
        }
        if (restaurantsUpdateAvailable) {
            downloadNewUpdates(context, restaurantsDownloadURL);
        } else {
            restaurantDownloadComplete = true;
        }
    }

    public void downloadNewUpdates(final Context context, final String url) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://data.surrey.ca");

        Retrofit retrofit = builder.build();
        FileDownloadClient fileDownloadClient = retrofit.create(FileDownloadClient.class);
        Call<ResponseBody> call = fileDownloadClient.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                File file;
                File newFile;
                if (url == restaurantsDownloadURL) {
                    String fileName = "newRestaurants.csv";
                    restaurantDownloadComplete = writeResponseBodyToDisk(response.body(), context, fileName);
                    file = context.getFileStreamPath("restaurants.csv");
                    if (file.isFile()) {
                        file.delete();
                    }
                    newFile = context.getFileStreamPath(fileName);
                } else {
                    String fileName = "newInspections.csv";
                    restaurantDownloadComplete = writeResponseBodyToDisk(response.body(), context, fileName);
                    file = context.getFileStreamPath("inspections.csv");
                    if (file.isFile()) {
                        file.delete();
                    }
                    newFile = context.getFileStreamPath(fileName);
                }
                newFile.renameTo(file);
                Date date = new Date();
                SharedPreferences prefs = context.getSharedPreferences(LAST_DOWNLOAD, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(LAST_DOWNLOAD, date.getTime());
                editor.apply();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, Context context, String name) {
        try {
            byte[] fileReader = new byte[4096];
            InputStream inputStream = null;
            inputStream = body.byteStream();
            FileOutputStream outputStream;
            outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            int read = inputStream.read(fileReader);
            while (read != -1) {
                outputStream.write(fileReader, 0, read);
                read = inputStream.read(fileReader);
            }
            outputStream.close();
            return true;
        } catch (Exception e) {
            Log.wtf("Download", "Error: "+ e);
            return false;
        }
    }

    public boolean downloadComplete() {

        return restaurantDownloadComplete && inspectionDownloadComplete;
    }

    public void cancelUpdate() {
        restaurantDownloadComplete = true;
        inspectionDownloadComplete = true;
    }

    public boolean isReady() {
        return restaurantsReady && inspectionsReady;
    }
}
