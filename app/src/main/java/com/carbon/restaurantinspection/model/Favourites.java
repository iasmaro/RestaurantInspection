package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class stores information of the favourite restaurants and saves the data using shared preferences.
 */
public class Favourites {
    private static ArrayList<String> favouriteList = new ArrayList<>();
    private static ArrayList<String> favouritesNewInspections = new ArrayList<>();
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

    public static void removeRestaurantToFavourites(String trackingNumber) {
        int index = favouriteList.indexOf(trackingNumber);
        favouriteList.remove(trackingNumber);
        dateList.remove(index);
        removeEmptySpaces();

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

    public static void setDateList(ArrayList<String> dateList) {
        Favourites.dateList = dateList;
        removeEmptySpaces();
    }

    private static ArrayList<String> getRecentInspections(Context context, ArrayList<String> list) {
        InspectionManager inspectionManager = InspectionManager.getInstance(context);
        ArrayList<String> recentInspections = new ArrayList<>();
        for (String trackingNumber : list) {
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(trackingNumber);
            if (inspections != null) {
                String newInspection = inspections.get(0).getStrInspectionDate();
                recentInspections.add(newInspection);
            } else {
                recentInspections.add("empty");
            }
        }

        return recentInspections;
    }

    private static ArrayList<String> getRecentHazardLevels(Context context) {
        InspectionManager inspectionManager = InspectionManager.getInstance(context);
        ArrayList<String> recentHazardLevel = new ArrayList<>();

        for (String trackingNumber : favouritesNewInspections) {
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(trackingNumber);
            if (inspections != null) {
                String newInspection = inspections.get(0).getHazardLevel();
                recentHazardLevel.add(newInspection);
            } else {
                recentHazardLevel.add("empty");
            }
        }

        return recentHazardLevel;
    }

    private static ArrayList<String> getRestaurantNames(Context context) {
        RestaurantManager restaurantManager = RestaurantManager.getInstance(context);
        ArrayList<Restaurant> restaurants = restaurantManager.getFiltered(favouritesNewInspections);
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

    private static ArrayList<String> getDisplayDate(Context context, ArrayList<String> list) {
        InspectionManager inspectionManager = InspectionManager.getInstance(context);
        ArrayList<String> recentInspections = new ArrayList<>();
        for (String trackingNumber : list) {
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(trackingNumber);
            if (inspections != null) {
                String newInspection = inspections.get(0).getInspectionDate(context);
                recentInspections.add(newInspection);
            } else {
                recentInspections.add("empty");
            }
        }
        return recentInspections;
    }

    public static ArrayList<String> getFavouriteInspectionsList(Context context) {
        ArrayList<String> favouriteInspections = new ArrayList<>();

        if (favouriteList.size() == 0) {
            return favouriteInspections;
        } else {
            findNewInspections(context);

            ArrayList<String> restaurantNames = getRestaurantNames(context);
            ArrayList<String> recentHazardLevel = getRecentHazardLevels(context);
            ArrayList<String> recentInspections = getDisplayDate(context, favouritesNewInspections);

            for (int i = 0; i < favouritesNewInspections.size(); i++) {
                String wasRated = context.getString(R.string.wasRated);
                String on = context.getString(R.string.on);
                String message = "" + restaurantNames.get(i) + " " + wasRated + " " +
                        recentHazardLevel.get(i) + " " + on + " " + recentInspections.get(i);
                favouriteInspections.add(message);
            }
            return favouriteInspections;
        }
    }

    private static void findNewInspections(Context context) {
        if (favouriteList != null) {
            ArrayList<String> recentInspections = getRecentInspections(context, favouriteList);

            for (int i = 0; i < recentInspections.size(); i++) {
                if ((!recentInspections.get(i).equals(dateList.get(i)) &&
                        (!recentInspections.get(i).equals("empty")))) {
                    favouritesNewInspections.add(favouriteList.get(i));
                    dateList.set(i, recentInspections.get(i));
                }
            }
            editor.putString(DATE_PREFS, arrayListToString(dateList));
            editor.apply();
        }
    }

    public static ArrayList<String> getFavouriteList() {
        return favouriteList;
    }
}