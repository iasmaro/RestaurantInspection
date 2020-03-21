package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.math.BigInteger;
import java.util.Date;

public class UpdateDownloader {
    private static final String LAST_DOWNLOAD = "LastDownload";
    private static final int MILLISECS_TO_HOURS = 3600000;
    private static final BigInteger DEFAULT_DATE = new BigInteger("1584687607039");
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
            // check city of surrey website for updates
            // return if date is less than saved date
            updatesAvailable = true;
        }
        return updatesAvailable;
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
