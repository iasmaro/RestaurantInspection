package com.carbon.restaurantinspection.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;

public class RestaurantManager implements Iterable<Restaurant>{
    private ArrayList<Restaurant> restaurantList;

    private static RestaurantManager instance;

    public static RestaurantManager getInstance(Context context) {
        if (instance == null) {
            instance = new RestaurantManager(context);
        }
        return instance;
    }

    private RestaurantManager(Context context) {
        RestaurantLoader loader = new RestaurantLoader();
        restaurantList = loader.loadRestaurantList(context);
    }

    public Restaurant getRestaurant(int index) {
        return restaurantList.get(index);
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }
}
