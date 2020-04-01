package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.carbon.restaurantinspection.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class Favourites {
    private static ArrayList<String> favouriteList = new ArrayList<>();
    private static Context contextOfApplication = MainActivity.getContextOfApplication();
    private static SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(contextOfApplication);
    private static SharedPreferences.Editor editor = preferences.edit();
    private static final String FAVOURITE_PREFS = "FavouriteList";

    public static void addRestaurantToFavourites(String trackingNumber) {
        favouriteList.add(trackingNumber);
        editor.putString(FAVOURITE_PREFS, arrayListToString());
        editor.apply();
    }

    public static void removeRestaurantToFavourites(String trackingNumber) {
        favouriteList.remove(trackingNumber);
        editor.putString(FAVOURITE_PREFS, arrayListToString());
        editor.apply();
    }

    public static boolean isRestaurantInFavourites(String trackingNumber) {
        for (String restaurant : favouriteList) {
            if (restaurant.equals(trackingNumber)) {
                return true;
            }
        }
        return false;
    }

    public static String arrayListToString() {
        String favouriteString = favouriteList.get(0);
        for (int i = 1; i < favouriteList.size(); i++) {
            favouriteString += "," + favouriteList.get(i);
        }
        return favouriteString;
    }

    public static ArrayList<String> stringToArrayList(String favouriteString) {
        String[] favouriteArray = favouriteString.split(",");
        ArrayList<String> favouriteList = new ArrayList<>();

        Collections.addAll(favouriteList, favouriteArray);
        return favouriteList;
    }

    public static void setFavouriteList(ArrayList<String> favouriteList) {
        Favourites.favouriteList = favouriteList;
    }

    public static ArrayList<String> getFavouriteList() {
        return favouriteList;
    }
}
