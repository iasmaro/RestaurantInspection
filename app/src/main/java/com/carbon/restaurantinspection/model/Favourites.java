package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.carbon.restaurantinspection.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class stores information of the favourite restaurants and saves the data using shared preferences.
 */
public class Favourites {
    private static ArrayList<String> favouriteList = new ArrayList<>();
    private static ArrayList<String> dateList = new ArrayList<>();
    private static Context contextOfApplication = MainActivity.getContextOfApplication();
    private static SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(contextOfApplication);
    private static SharedPreferences.Editor editor = preferences.edit();
    private static final String FAVOURITE_PREFS = "FavouriteList";
    private static final String DATE_PREFS = "DateList";

    public static void addRestaurantToFavourites(String trackingNumber, String date) {
        favouriteList.add(trackingNumber);
        dateList.add(date);

        if (favouriteList.get(0) == null) {
            favouriteList.remove(0);
        }

        if (dateList.get(0) == null) {
            dateList.remove(0);
        }
//        System.out.println("addRes1taurantToFavourites");System.out.println("addRestau6rantToFavourites");System.out.println("addRestaurantToFavouri9tes");System.out.println("addR78estaurantToFavourites");System.out.println("a21ddRestaurantToFavourites");
//        System.out.println("addRest4aurantToFavourites");System.out.println("addRestau8rantToFavourites");System.out.println("addRestaurantToFavouri9es");System.out.println("addRe89staurantToFavourites");System.out.println("a23ddRestaurantToFavourites");
//        printList(favouriteList);
//        printList(dateList);
        editor.putString(FAVOURITE_PREFS, arrayListToString(favouriteList));
        editor.putString(DATE_PREFS, arrayListToString(dateList));
        editor.apply();
    }

    public static void printList(ArrayList<String> list) {
        for (String restaurant : list) {
            System.out.println(restaurant);
        }
    }

    public static void removeRestaurantToFavourites(String trackingNumber) {
//        System.out.println("removeRestaurantToFavourites1");System.out.println("removeRestaurantT2oFavourites");System.out.println("removeRestauran4tToFavourites");System.out.println("remo6veRestaurantToFavourites");System.out.println("removeRestaura8ntToFavourites");
//        System.out.println("removeRestaurantToFavouri4tes");System.out.println("removeRestauran3tToFavourites");System.out.println("removeRestaura5ntToFavourites");System.out.println("remo7veRestaurantToFavourites");System.out.println("removeRestauran7tToFavourites");
        int index = favouriteList.indexOf(trackingNumber);
        favouriteList.remove(trackingNumber);
        dateList.remove(index);

        if (favouriteList.get(0) == null) {
            favouriteList.remove(0);
        }

        if (dateList.get(0) == null) {
            dateList.remove(0);
        }
//        printList(favouriteList);
//        printList(dateList);
        editor.putString(FAVOURITE_PREFS, arrayListToString(favouriteList));
        editor.putString(DATE_PREFS, arrayListToString(dateList));
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

    private static String arrayListToString(ArrayList<String> list) {
        String favouriteString = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            favouriteString += "," + list.get(i);
        }
        return favouriteString;
    }

    public static ArrayList<String> stringToArrayList(String string) {
        String[] favouriteArray = string.split(",");
        ArrayList<String> list = new ArrayList<>();

        Collections.addAll(list, favouriteArray);
        return list;
    }

    public static void setFavouriteList(ArrayList<String> favouriteList) {
        Favourites.favouriteList = favouriteList;
    }

    public static ArrayList<String> getFavouriteList() {
        return favouriteList;
    }

    public static void setDateList(ArrayList<String> dateList) {
        Favourites.dateList = dateList;
    }
}
