package com.carbon.restaurantinspection.ui;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

import java.util.ArrayList;

public class RestaurantListActivity extends AppCompatActivity {

    //ArrayList<String> name = new ArrayList<>();

    // calls RestaurantManager class
    private RestaurantManager restaurantManager;
    //private Restaurant restaurant;

    //restaurantsArray restaurant = new restaurantsArray();
    //private ArrayList<restaurants> restList = restaurant.getRestaurantList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list_activity);

        // Get that one instance that RestaurantManager class produced
        restaurantManager = RestaurantManager.getInstance(this);
        //Restaurant restaurant = new Restaurant();

        populateRestaurantListView();
    }

    private void populateRestaurantListView() {
        //Log.d("Test", "testing");
        //create an array list of restaurant names
        /**for (Restaurant restaurants: restaurantManager) {
            name.add(restaurants.getName());
        }**/

        // Build adapter
        //ArrayAdapter<Restaurant> adapter = new ArrayAdapter<Restaurant>(this,R.layout.display_restaurant_list_activity);


        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        //ArrayAdapter<restaurant> adapter = new MyListAdapter();
        //configure list view

        ListView list = (ListView) findViewById(R.id.restaurant_list_view);
        list.setAdapter(adapter);

    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter(){
            super(RestaurantListActivity.this, R.layout.display_restaurant_list_activity, restaurantManager.getRestaurantList());
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.display_restaurant_list_activity, parent,false);
            }

            // find the restaurant
            String currentRestaurants = restaurantManager.getRestaurant(position).getName();

            //fill in the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_restaurant_icon);
            imageView.setImageResource(R.drawable.a_w_restaurant_icon);

            TextView restaurantNameText = (TextView) itemView.findViewById(R.id.restaurant_name_textview);
            restaurantNameText.setText(currentRestaurants);

            return itemView;
        }
    }
}
