package com.carbon.restaurantinspection.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

import java.util.ArrayList;

public class RestaurantListActivity extends AppCompatActivity {

    // calls RestaurantManager class
    private RestaurantManager restaurantManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list_activity);

        // Get that one instance that RestaurantManager class produced
        restaurantManager = RestaurantManager.getInstance(this);

        populateRestaurantListView();
    }

    private void populateRestaurantListView() {

        // Create lenses using the constructor from Lens class
        Restaurant restaurant0 = new Restaurant("SDFO-8HKP7E", "Pattullo A&W", "12808 King George Blvd", "Surrey", "Restaurant", 49.20610961, -122.8668064);
        //Restaurant restaurant1 = new Restaurant( "Tamron", 90, 2.80);
        //Restaurant restaurant2 = new Restaurant("Sigma", 200, 2.80);
        //Restaurant restaurant3 = new Restaurant( "Nikon", 200, 4.0);
        // add lenses into a list
        restaurantManager.add(restaurant0);
        //restaurantManager.add(restaurant1);
        //restaurantManager.add(restaurant2);
        //restaurantManager.add(restaurant3);

        ArrayList <String>name= new ArrayList<>();
        //int i = 0;
        for (Restaurant restaurants: restaurantManager) {
            name.add( restaurants.getName());

        }

        //String restaurant1 = restaurantManager.getRestaurant(0).get(1);

        // create array of restaurant names
        //String[] restaurantNames = {restaurantNames1, "macdonalds"};

        // Build adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.display_restaurant_list_activity, name);
        //configure list view
        ListView list = (ListView) findViewById(R.id.restaurant_list_view);
        list.setAdapter(adapter);
    }
}
