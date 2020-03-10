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

/**
 * RestaurantLoader Class loads restaurant information from a csv file,
 * and creates a Restaurant object for each line from the csv file.
 * It adds the Restaurant objects into an ArrayList.
 */
public class RestaurantLoader {
    private final String DELIMITER = ",";
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();

    public ArrayList<Restaurant> loadRestaurantList(Context context) {

        InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            reader.readLine();

            line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(DELIMITER);
                addRestaurant(tokens);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.wtf("RestaurantLoader", "Error reading data file on line " + line, e);
        }

        return restaurantList;
    }

    private void addRestaurant(String[] restaurantInfo) {
        double latitude = Double.parseDouble(restaurantInfo[5]);
        double longitude = Double.parseDouble(restaurantInfo[6]);
        String trackingNumber = restaurantInfo[0].split("\"")[1];
        Restaurant restaurant = new Restaurant(trackingNumber, restaurantInfo[1], restaurantInfo[2],
                restaurantInfo[3], restaurantInfo[4], latitude, longitude);
        restaurantList.add(restaurant);
    }
}
