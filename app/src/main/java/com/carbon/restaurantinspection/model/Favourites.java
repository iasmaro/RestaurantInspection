package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.Intent;

import com.carbon.restaurantinspection.ui.InspectionDetailsActivity;

import java.util.ArrayList;

public class Favourites {
    private static ArrayList<String> favouriteList = new ArrayList<>();
    private static final String EXTRA_TRACKING_NUMBER = "com.carbon.restaurantinspection.model.Favourite.trackingNumber";

    public static void addRestaurantToFavourites(String trackingNumber) {
        favouriteList.add(trackingNumber);
        System.out.println("here");System.out.println("here");System.out.println("here");System.out.println("here");
        for (int i = 0; i < favouriteList.size(); i++) {
            System.out.println(favouriteList.get(i));
        }
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
