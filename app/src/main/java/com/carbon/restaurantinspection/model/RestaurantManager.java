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
        import java.util.Iterator;

public class RestaurantManager implements Iterable<Restaurant>{
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();

    private static RestaurantManager instance;

    private RestaurantManager(Context context) {
        File csvFile = context.getFileStreamPath("restaurants.csv");
        CSVLoader loader = new CSVLoader();
        ArrayList<String> file;
        if (csvFile.isFile()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(csvFile);
                file = loader.readCSV(fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);
                file = loader.readCSV(is);
            }
        } else {
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
        String trackingNumber = restaurantInfo[0].split("\"")[1];
        String restaurantName = restaurantInfo[1].split("\"")[1];
        String restaurantAddress = restaurantInfo[2].split("\"")[1];
        String restaurantCity = restaurantInfo[3].split("\"")[1];
        String type = restaurantInfo[4].split("\"")[1];
        Restaurant restaurant = new Restaurant(trackingNumber, restaurantName, restaurantAddress,
                restaurantCity, type, latitude, longitude);
        restaurantList.add(restaurant);
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
