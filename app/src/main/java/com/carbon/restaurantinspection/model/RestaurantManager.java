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
    private ArrayList<Restaurant> filterList = new ArrayList<>();
    private String searchTerm;
    private ArrayList<Restaurant> searchFilterList = new ArrayList<>();

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
        if (searchFilterList.size() > 0) {
            return searchFilterList.get(index);
        } else if (searchList.size() > 0) {
            return searchList.get(index);
        } else if (filterList.size() > 0) {
            return filterList.get(index);
        } else {
            return restaurantList.get(index);
        }
    }


    // gets the entire list of restaurants
    public ArrayList<Restaurant> getRestaurantList(){
        if (searchFilterList.size() > 0) {
            return searchFilterList;
        } else if (searchList.size() > 0) {
            return searchList;
        } else if (filterList.size() > 0) {
            return filterList;
        } else {
            return restaurantList;
        }
    }

    public ArrayList<Restaurant> searchRestaurants(String search) {
        searchTerm = search;
        ArrayList<Restaurant> searched = searchList;
        if (filterList.size() > 0) {
            searchFiltered();
            searched = searchFilterList;
        }
        searchList.clear();
        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getName().toLowerCase().contains(search)) {
                searchList.add(restaurant);
            }
        }
        return searched;
    }


    public void clearSearch() {
        searchTerm = null;
        searchList.clear();
        searchFilterList.clear();
    }

    public String getSearchTerm() {
        return searchTerm;
    }



    public ArrayList<Restaurant> getFiltered (ArrayList<String> trackingNumbers) {
        ArrayList<Restaurant> filtered = new ArrayList<>();
        for (String trackingNumber: trackingNumbers) {
            filtered.add(restaurantHashtable.get(trackingNumber));
        }
        return filtered;
    }

    public ArrayList<Restaurant> filter(ArrayList<String> trackingNumbers) {
        filterList.clear();
        ArrayList<Restaurant> filtered = filterList;
        for (String trackingNumber: trackingNumbers) {
            Restaurant restaurant = restaurantHashtable.get(trackingNumber);
            if (restaurant != null) {
                filterList.add(restaurant);
            }
        }
        if (searchList.size() > 0) {
            searchFiltered();
            filtered = searchFilterList;
        }
        return filtered;
    }

    public void clearFilter() {
        filterList.clear();
        searchFilterList.clear();
    }

    private void searchFiltered() {
        searchFilterList.clear();
        for (Restaurant restaurant : filterList) {
            if (restaurant.getName().toLowerCase().contains(searchTerm)) {
                searchFilterList.add(restaurant);
            }
        }
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }
}
