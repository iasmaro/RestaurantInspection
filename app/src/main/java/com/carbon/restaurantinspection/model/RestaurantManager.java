package com.carbon.restaurantinspection.model;

import android.content.Context;

import com.carbon.restaurantinspection.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * RestaurantManager class is a singleton class that manages a list of Restaurants.
 */
public class RestaurantManager implements Iterable<Restaurant>{
    private Hashtable<String, Restaurant> restaurantHashtable = new Hashtable<>();
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private ArrayList<Restaurant> searchList = new ArrayList<>();

    private static RestaurantManager instance;

    private RestaurantManager(Context context) {
        File csvFile = context.getFileStreamPath("restaurants.csv");
        CSVLoader loader = new CSVLoader();
        ArrayList<String> file;
        try {
            FileInputStream fileInputStream = new FileInputStream(csvFile);
            file = loader.readCSV(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);
            file = loader.readCSV(is);
        }
        parseFile(file);
        sortRestaurants();
    }

    public ArrayList<Restaurant> searchRestaurants(String search) {
        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getName().contains(search)) {
                searchList.add(restaurant);
            }
        }
        return searchList;
    }

    public void clearSearch() {
        searchList.clear();
    }

    private void sortRestaurants() {
        Collections.sort(restaurantList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant restaurant, Restaurant otherRestaurant) {
                return  restaurant.getName().compareTo(otherRestaurant.getName());
            }
        });
    }

    private void parseFile(ArrayList<String> file) {
        for (String line : file) {
            String[] tokens = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            addRestaurant(tokens);
        }
    }

    private void addRestaurant(String[] restaurantInfo) {
        double latitude = Double.parseDouble(restaurantInfo[5]);
        double longitude = Double.parseDouble(restaurantInfo[6]);
        String[] trackingNumberList = restaurantInfo[0].split("\"");
        String trackingNumber;
        if (trackingNumberList.length > 1) {
            trackingNumber = trackingNumberList[1];
        } else {
            trackingNumber = trackingNumberList[0];
        }
        String[] restaurantNameList = restaurantInfo[1].split("\"");
        String restaurantName;
        if (restaurantNameList.length > 1) {
            restaurantName = restaurantNameList[1];
        } else {
            restaurantName = restaurantNameList[0];
        }
        String[] addressList = restaurantInfo[2].split("\"");
        String restaurantAddress;
        if (addressList.length > 1) {
            restaurantAddress = addressList[1];
        } else {
            restaurantAddress = addressList[0];
        }
        String[] cityList = restaurantInfo[3].split("\"");
        String restaurantCity;
        if (cityList.length > 1) {
            restaurantCity = cityList[1];
        } else {
            restaurantCity = cityList[0];
        }
        String[] typeList = restaurantInfo[4].split("\"");
        String type;
        if (typeList.length > 1) {
            type = typeList[1];
        } else {
            type = typeList[0];
        }
        Restaurant restaurant = new Restaurant(trackingNumber, restaurantName, restaurantAddress,
                restaurantCity, type, latitude, longitude);
        restaurantList.add(restaurant);
        restaurantHashtable.put(trackingNumber, restaurant);
    }

    public static RestaurantManager getInstance(Context context) {
        if (instance == null) {
            instance = new RestaurantManager(context);
        }
        return instance;
    }

    public Restaurant getRestaurant(int index) {
        return restaurantList.get(index);
    }


    // gets the entire list of restaurants
    public ArrayList getRestaurantList(){
        return restaurantList;
    }

    public void setRestaurantList(ArrayList <Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public ArrayList<Restaurant> getFiltered (ArrayList<String> trackingNumbers) {
        ArrayList<Restaurant> filtered = new ArrayList<>();
        for (String trackingNumber: trackingNumbers) {
            filtered.add(restaurantHashtable.get(trackingNumber));
        }
        return filtered;
    }

    // Supports adding restaurants
    public void add(Restaurant restaurant) {
        restaurantList.add(restaurant);
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }
}
