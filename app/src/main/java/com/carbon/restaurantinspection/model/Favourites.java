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
        removeEmptySpaces();

        editor.putString(FAVOURITE_PREFS, arrayListToString(favouriteList));
        editor.putString(DATE_PREFS, arrayListToString(dateList));
        editor.apply();
    }

    private static void removeEmptySpaces() {
        if (favouriteList.get(0).equals("") && dateList.get(0).equals("")) {
            favouriteList.remove(0);
            dateList.remove(0);
        }
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
        removeEmptySpaces();

//        printList(favouriteList);
//        printList(dateList);
        editor.putString(FAVOURITE_PREFS, arrayListToString(favouriteList));
        editor.putString(DATE_PREFS, arrayListToString(dateList));
        editor.apply();
    }

    public static String getDate(String trackingNumber) {
        int index = favouriteList.indexOf(trackingNumber);
        return dateList.get(index);
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
//        removeEmptySpaces();
    }

    public static ArrayList<String> getFavouriteList() {
        return favouriteList;
    }

    public static void setDateList(ArrayList<String> dateList) {
        Favourites.dateList = dateList;
        removeEmptySpaces();
    }

    private static ArrayList<String> getRecentInspections(Context context) {
        InspectionManager inspectionManager = InspectionManager.getInstance(context);
        ArrayList<String> recentInspections = new ArrayList<>();
        for (String trackingNumber : favouriteList) {
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(trackingNumber);
            String newInspection = inspections.get(0).getInspectionDate(context);
            recentInspections.add(newInspection);
        }

        return recentInspections;
    }

    private static ArrayList<String> getRecentHazardLevels(Context context) {
        InspectionManager inspectionManager = InspectionManager.getInstance(context);
        ArrayList<String> recentHazardLevel = new ArrayList<>();

        for (String trackingNumber : favouriteList) {
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(trackingNumber);
            String newInspection = inspections.get(0).getHazardLevel();
            recentHazardLevel.add(newInspection);
        }

        return recentHazardLevel;
    }

    private static ArrayList<String> getRestaurantNames(Context context) {
        RestaurantManager restaurantManager = RestaurantManager.getInstance(context);
        ArrayList<Restaurant> restaurants = restaurantManager.getFiltered(favouriteList);
        ArrayList<String> restaurantNames = new ArrayList<>();
        if (restaurants.isEmpty()) {
            return restaurantNames;
        } else {
            for (Restaurant restaurant : restaurants) {
                if (restaurant != null) {
                    restaurantNames.add(restaurant.getName());
                }
            }

            return restaurantNames;
        }
    }

    public static ArrayList<FavouriteInspections> getFavouriteInspectionsList(Context context) {
        ArrayList<FavouriteInspections> favouriteInspections = new ArrayList<>();
        if (favouriteList.size() == 0) {

            return null;
        } else {
            System.out.println("in if");System.out.println("in if");System.out.println("in if");System.out.println("in if");System.out.println(favouriteList.size());
            System.out.println("check");System.out.println("check");System.out.println("check");
            System.out.println(favouriteList.get(0) == null);System.out.println(favouriteList.get(0).equals(" "));
            System.out.println(favouriteList.get(0).equals(""));
            ArrayList<String> restaurantNames = getRestaurantNames(context);
            ArrayList<String> recentHazardLevel = getRecentHazardLevels(context);
            ArrayList<String> recentInspections = getRecentInspections(context);

            for (int i = 0; i < favouriteList.size(); i++) {
                favouriteInspections.add(new FavouriteInspections(restaurantNames.get(i),
                        recentInspections.get(i), recentHazardLevel.get(i)));
            }
            return favouriteInspections;
        }
    }
}