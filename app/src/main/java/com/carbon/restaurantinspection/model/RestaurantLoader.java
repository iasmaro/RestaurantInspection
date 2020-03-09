package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.util.Log;

import com.carbon.restaurantinspection.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RestaurantLoader {
    private final String DELIMITER = ",";
    private ArrayList<Restaurant> restaurantList;

    public ArrayList<Restaurant> loadRestaurantList(Context context) {
        restaurantList = new ArrayList<>();
        InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(DELIMITER);
                addRestaurant(tokens);
            }
        } catch (IOException e) {
            Log.wtf("RestaurantLoader", "Error reading data file on line " + line, e);
        }

        return restaurantList;
    }

    private void addRestaurant(String[] restaurantInfo) {
        double latitude = Double.parseDouble(restaurantInfo[5]);
        double longitude = Double.parseDouble(restaurantInfo[6]);
        Restaurant restaurant = new Restaurant(restaurantInfo[0], restaurantInfo[1], restaurantInfo[2],
                restaurantInfo[3], restaurantInfo[4], latitude, longitude);
        restaurantList.add(restaurant);
    }
}
