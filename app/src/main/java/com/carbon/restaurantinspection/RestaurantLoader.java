package com.carbon.restaurantinspection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RestaurantLoader {
    final String CSV_NAME = "restaurants_itr1.csv";
    final String DELIMITER = ",";

    public ArrayList<Restaurant> loadRestaurantList() throws IOException {
        ArrayList<Restaurant> restaurantList = new ArrayList<>();
        Path filePath = Paths.get(CSV_NAME);

        BufferedReader reader = Files.newBufferedReader(filePath);

        String headerLine = reader.readLine();
        String currentLine = reader.readLine();

        while (currentLine != null){
            String[] restaurantInfo = currentLine.split(DELIMITER);

            restaurantList.add(makeNewRestaurant(restaurantInfo));
            currentLine = reader.readLine();
        }

        return restaurantList;
    }

    private Restaurant makeNewRestaurant(String[] restaurantInfo) {
        String trackingNumber = restaurantInfo[0];
        String name = restaurantInfo[1];
        String physicalAddress = restaurantInfo[2];
        String city = restaurantInfo[3];
        String factype = restaurantInfo[4];
        double latitude = Double.parseDouble(restaurantInfo[5]);
        double longitude = Double.parseDouble(restaurantInfo[6]);

        return new Restaurant(trackingNumber, name, physicalAddress, city, factype, latitude, longitude);
    }
}
