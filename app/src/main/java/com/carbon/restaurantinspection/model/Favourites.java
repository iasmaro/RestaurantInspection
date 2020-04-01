package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.carbon.restaurantinspection.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class Favourites {
    private static ArrayList<String> favouriteList = new ArrayList<>();
    private static ArrayList<String> dateList = new ArrayList<>();
    private static Context contextOfApplication = MainActivity.getContextOfApplication();
    private static SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(contextOfApplication);
    private static SharedPreferences.Editor editor = preferences.edit();
    private static final String FAVOURITE_PREFS = "FavouriteList";

    public static void addRestaurantToFavourites(String trackingNumber) {
        favouriteList.add(trackingNumber);
        System.out.println("addRes1taurantToFavourites");System.out.println("addRestau6rantToFavourites");System.out.println("addRestaurantToFavouri9tes");System.out.println("addR78estaurantToFavourites");System.out.println("a21ddRestaurantToFavourites");
        System.out.println("addRest4aurantToFavourites");System.out.println("addRestau8rantToFavourites");System.out.println("addRestaurantToFavouri9es");System.out.println("addRe89staurantToFavourites");System.out.println("a23ddRestaurantToFavourites");
        printList();
        editor.putString(FAVOURITE_PREFS, arrayListToString());
        editor.apply();
    }

    public static void printList() {
        for (String restaurant : favouriteList) {
            System.out.println(restaurant);
        }
    }

    public static void removeRestaurantToFavourites(String trackingNumber) {
        System.out.println("removeRestaurantToFavourites1");System.out.println("removeRestaurantT2oFavourites");System.out.println("removeRestauran4tToFavourites");System.out.println("remo6veRestaurantToFavourites");System.out.println("removeRestaura8ntToFavourites");
        System.out.println("removeRestaurantToFavouri4tes");System.out.println("removeRestauran3tToFavourites");System.out.println("removeRestaura5ntToFavourites");System.out.println("remo7veRestaurantToFavourites");System.out.println("removeRestauran7tToFavourites");
        favouriteList.remove(trackingNumber);
        printList();
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
