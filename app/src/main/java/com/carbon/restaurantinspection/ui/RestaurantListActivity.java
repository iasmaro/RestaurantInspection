package com.carbon.restaurantinspection.ui;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
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

    // calls RestaurantManager class
    //private RestaurantManager restaurantManager;


    restaurantsArray restaurant = new restaurantsArray();
    private ArrayList<restaurants> restList = restaurant.getRestaurantList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list_activity);

        // Get that one instance that RestaurantManager class produced
        //restaurantManager = RestaurantManager.getInstance(this);


        populateRestaurantListView();
    }

    private void populateRestaurantListView() {

        // Create lenses using the constructor from Lens class
        //Restaurant restaurant0 = new Restaurant(R.drawable.a_w_restaurant_icon, "SDFO-8HKP7E", "Pattullo A&W", "12808 King George Blvd", "Surrey", "Restaurant", 49.20610961, -122.8668064);
        //Restaurant restaurant1 = new Restaurant( R.drawable.a_w_restaurant_icon,"SHEN-B7BNSR", "Lee Yuen Seafood Restaurant", "1812 152 St", "Surrey", "Restaurant", 49.03508252, -122.80086843);
        //Restaurant restaurant2 = new Restaurant(R.drawable.a_w_restaurant_icon, "NOSU-CHNUM", "The Unfindable Bar", "12345 67 Ave", "Surrey", "Restaurant", 49.14214908, -122.86815856);
        //Restaurant restaurant3 = new Restaurant( "Nikon", 200, 4.0);
        // add lenses into a list
        //restaurantManager.add(restaurant0);
        //restaurantManager.add(restaurant1);
        //restaurantManager.add(restaurant2);
        //restaurantManager.add(restaurant3);

        // create an array list of restaurant names
        //for (Restaurant restaurants: restaurantManager) {
        //    name.add( restaurants.getName());
        //}

        // Build adapter
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.display_restaurant_list_activity, name);
        ArrayAdapter<restaurants> adapter = new MyListAdapter();
        //configure list view

        ListView list = (ListView) findViewById(R.id.restaurant_list_view);
        list.setAdapter(adapter);

    }
    private class MyListAdapter extends ArrayAdapter<restaurants> {
        public MyListAdapter(){
            super(RestaurantListActivity.this, R.layout.display_restaurant_list_activity, restList);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.display_restaurant_list_activity, parent,false);
            }

            // find the restaurant
            restaurants currentRestaurants = restList.get(position);

            //fill in the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_restaurant_icon);
            imageView.setImageResource(currentRestaurants.getIconID());

            TextView restaurantNameText = (TextView) itemView.findViewById(R.id.restaurant_name_textview);
            restaurantNameText.setText(currentRestaurants.getname());

            return itemView;
        }
    }


//-------------------------------------------------------------------------------------------------------------------------

    public class restaurantsArray{
        private ArrayList<restaurants> restaurantList = new ArrayList<>();

        // getter for the list of restaurant names
        public ArrayList<restaurants> getRestaurantList(){
            return restaurantList;
        }

        public restaurantsArray(){
            this.restaurantList.add(new restaurants(R.drawable.a_w_restaurant_icon, "Pattullo A&W"));
            this.restaurantList.add(new restaurants(R.drawable.seafood_icon, "Lee Yuen Seafood Restaurant"));
            this.restaurantList.add(new restaurants(R.drawable.beer_icon, "The Unfindable Bar"));
            this.restaurantList.add(new restaurants(R.drawable.a_w_restaurant_icon, "Lee Yuen Seafood Restaurant"));
            this.restaurantList.add(new restaurants(R.drawable.a_w_restaurant_icon, "Top in Town Pizza"));
            this.restaurantList.add(new restaurants(R.drawable.a_w_restaurant_icon, "104 Sushi & Co."));
            this.restaurantList.add(new restaurants(R.drawable.a_w_restaurant_icon, "Top In Town Pizza"));
            this.restaurantList.add(new restaurants(R.drawable.a_w_restaurant_icon, "Zugba Flame Grilled Chicken"));
        }
    }

//  restaurants class --------------------------------------------------------
    public class restaurants{
        private String name;
        private int iconID;

        //constructor for the restaurant name, and icon
        public restaurants(int iconID, String name) {
            this.name = name;
            this.iconID = iconID;
        }

        // gets the icon
        public int getIconID() {
            return iconID;
        }

        // gets the name of the restaurant
        public String getname() {
            return name;
        }
    }
}
