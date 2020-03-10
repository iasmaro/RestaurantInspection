package com.carbon.restaurantinspection.model;

        import android.content.Context;

        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;

public class RestaurantManager implements Iterable<Restaurant>{
    private ArrayList<Restaurant> restaurantList;

    private static RestaurantManager instance;

    private RestaurantManager(Context context) {
        RestaurantLoader loader = new RestaurantLoader();
        restaurantList = loader.loadRestaurantList(context);
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


    // Supports adding restaurants
    public void add(Restaurant restaurant) {
        restaurantList.add(restaurant);
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }
}
