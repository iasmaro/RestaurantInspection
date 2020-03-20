package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class UpdateDownloader {
    public static final String LAST_DOWNLOAD = "LastDownload";
    public static final int MILLISECS_TO_HOURS = 3600000;
    public boolean checkForUpdates(Context context) {
        Date date = new Date();
        SharedPreferences prefs = context.getSharedPreferences(LAST_DOWNLOAD, Context.MODE_PRIVATE);
        String savedDate = prefs.getString(LAST_DOWNLOAD, "19900101");
        // return if date - savedDate > 24 hours
        return false;
    }
    public boolean updatesAvailable(Context context) {
        // check city of surrey website for updates
        // return if date is less than saved date
        return false;
    }

    public void downloadUpdates(Context context) {
        // Download updates from city of surrey website
        // allow user to cancel if necessary
        // once download is complete, remove current data set
        // rename newly downloaded data set
        // update savedDownloadDate
    }

    public int downloadProgress() {
        // report download progress to user
        return 0;
    }

    public void cancelUpdate() {
        // cancel updates
    }
}
