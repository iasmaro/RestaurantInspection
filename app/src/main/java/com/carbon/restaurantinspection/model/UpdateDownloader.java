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
import com.carbon.restaurantinspection.service.FileDownloadClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Check city of Surrey server for updates.
 * Download updates if any available and user decides to download them.
 * Cancel ongoing download if user decides to cancel download.
 */
public class UpdateDownloader {
    private static final String LAST_RESTAURANT_DOWNLOAD = "LastDownload";
    private static final String LAST_INSPECTION_DOWNLOAD = "LastDownload";
    private static final int MILLISECS_TO_HOURS = 3600000;
    private static final BigInteger DEFAULT_DATE = new BigInteger("1574686607039");
    private static final String RESTAURANTS_URL =
            "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String INSPECTIONS_URL =
            "http://data.surrey.ca/api/3/action/package_show?" +
                    "id=fraser-health-restaurant-inspection-reports";
    private JSONObject restaurantsJson;
    private JSONObject inspectionsJson;
    private String restaurantsDownloadURL;
    private String inspectionsDownloadURL;
    private boolean restaurantsUpdateAvailable;
    private boolean inspectionsUpdateAvailable;
    private long lastRestaurantUpdate;
    private long lastInspectionUpdate;
    private boolean restaurantsReady = false;
    private boolean inspectionsReady = false;
    private boolean restaurantDownloadComplete = false;
    private boolean inspectionDownloadComplete = false;
    private Call<ResponseBody> restaurantsDownload;
    private Call<ResponseBody> inspectionsDownload;

    public UpdateDownloader(Context context) {
        getRestaurant(context, RESTAURANTS_URL);
        getRestaurant(context, INSPECTIONS_URL);
    }

    private boolean checkForUpdates(Context context) {
        Date date = new Date();
        SharedPreferences restaurantPrefs = context
                .getSharedPreferences(LAST_RESTAURANT_DOWNLOAD, Context.MODE_PRIVATE);
        lastRestaurantUpdate = restaurantPrefs
                .getLong(LAST_RESTAURANT_DOWNLOAD, DEFAULT_DATE.longValue());
        long restaurantHours = (date.getTime() - lastRestaurantUpdate)/MILLISECS_TO_HOURS;
        SharedPreferences inspectionPrefs = context
                .getSharedPreferences(LAST_INSPECTION_DOWNLOAD, Context.MODE_PRIVATE);
        lastInspectionUpdate = inspectionPrefs
                .getLong(LAST_INSPECTION_DOWNLOAD, DEFAULT_DATE.longValue());
        long inspectionHours = (date.getTime() - lastRestaurantUpdate)/MILLISECS_TO_HOURS;
        return restaurantHours >= 20 || inspectionHours >= 20;
    }
    public boolean updatesAvailable(Context context) {
        boolean updatesAvailable = false;
        if (checkForUpdates(context) && restaurantsReady && inspectionsReady) {
            try {
                Date restaurantsUpdate = getDate(restaurantsJson.getString("last_modified"));
                restaurantsDownloadURL = restaurantsJson.getString("url");
                Date inspectionsUpdate = getDate(inspectionsJson.getString("last_modified"));
                inspectionsDownloadURL = inspectionsJson.getString("url");
                restaurantsUpdateAvailable = restaurantsUpdate.getTime() > lastRestaurantUpdate;
                inspectionsUpdateAvailable = inspectionsUpdate.getTime() > lastInspectionUpdate;
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
        Date inspectionDate;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            inspectionDate = dateFormat.parse(correctDate);
            return inspectionDate;
        } catch (ParseException e) {
            return new Date();
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
        // used retrofit download tutorial from
        // https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://data.surrey.ca");
        Retrofit retrofit = builder.build();
        FileDownloadClient fileDownloadClient = retrofit.create(FileDownloadClient.class);
        Call<ResponseBody> call;
        call = fileDownloadClient.downloadFile(url);
        if (url == restaurantsDownloadURL) {
            restaurantsDownload = call;
        } else {
            inspectionsDownload = call;
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                if (url == restaurantsDownloadURL) {
                    restaurantDownloadComplete = true;
                    storeData("restaurants.csv", "newRestaurants.csv",
                            context, response.body());
                    updateSavedDate(context, LAST_RESTAURANT_DOWNLOAD);
                } else {
                    inspectionDownloadComplete = true;
                    storeData("inspections.csv", "newInspections.csv",
                            context, response.body());
                    updateSavedDate(context, LAST_INSPECTION_DOWNLOAD);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.wtf("UpdateDownloader", "Error:" + t);
                }
            }
        });
    }

    private void storeData(String fileName, String newFileName, Context context, ResponseBody body) {
        File file;
        File newFile;
        writeResponseBodyToDisk(body, context, newFileName);
        file = context.getFileStreamPath(fileName);
        if (file.isFile()) {
            file.delete();
        }
        newFile = context.getFileStreamPath(newFileName);
        newFile.renameTo(file);
    }

    private void updateSavedDate(Context context, String pref) {
        Date date = new Date();
        SharedPreferences prefs = context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(pref, date.getTime());
        editor.apply();
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
        // used cancel request tutorial from
        // https://futurestud.io/tutorials/retrofit-2-cancel-requests
        if (restaurantsDownload != null) {
            restaurantsDownload.cancel();
        } if (inspectionsDownload != null){
            inspectionsDownload.cancel();
        }
        restaurantDownloadComplete = true;
        inspectionDownloadComplete = true;
    }

    public boolean isReady() {
        return restaurantsReady && inspectionsReady;
    }
}
