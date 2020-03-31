package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.Intent;

import com.carbon.restaurantinspection.ui.InspectionDetailsActivity;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class Favourites {
    private static ArrayList<String> favouriteList = new ArrayList<>();
    private static final String EXTRA_TRACKING_NUMBER = "com.carbon.restaurantinspection.model.Favourite.trackingNumber";

    public static void addRestaurantToFavourites(String trackingNumber) {
        favouriteList.add(trackingNumber);
    }

    public static boolean isRestaurantInFavourites(String trackingNumber) {
        int i = 0;
        for (String restaurant : favouriteList) {
            if (restaurant.equals(trackingNumber)) {
                return true;
            }
        }
        return false;
    }

    public static void removeRestaurantToFavourites(String trackingNumber) {
        favouriteList.remove(trackingNumber);
    }

    private void extractDataFromIntent() {
        Intent intent = new Intent();
        String trackingNumber = intent.getStringExtra(EXTRA_TRACKING_NUMBER);
        favouriteList.add(trackingNumber);
    }

    public static Intent makeIntent(Context context, String trackingNumber) {
        Intent intent = new Intent(context, InspectionDetailsActivity.class);
        intent.putExtra(EXTRA_TRACKING_NUMBER, trackingNumber);
        return intent;
    }

    public ArrayList<String> getFavouriteList() {
        return favouriteList;
    }
}
