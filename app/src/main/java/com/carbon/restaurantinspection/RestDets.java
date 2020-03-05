package com.carbon.restaurantinspection;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionManger;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

public class RestDets extends AppCompatActivity {
    private int index = 1;
    private RestaurantManager RestManager;
    private InspectionManger InsManager;
    TextView addy;
    TextView coordinates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestManager = RestaurantManager.getInstance(this);
        InsManager =  InspectionManger.getInstance(this);
        setContentView(R.layout.activity_rest_dets);
        addy = findViewById(R.id.textView2);
        coordinates = findViewById(R.id.textView3);
        updateAddress();
        getIntents();
        displayInspections();
    }

    private void getIntents() {
        Intent intent = getIntent();
        index = intent.getIntExtra("ca/sfu/restaurantinspections/MainActivity.java:14", 0);
    }

    private void displayInspections() {
    }

    private void updateAddress() {
        Restaurant myRest = RestManager.getRestaurant(index);
        String str = myRest.getPhysicalAddress();
        addy.setText(str);
        String longitude = Double.toString(myRest.getLongitude());
        String latitude = Double.toString(myRest.getLatitude());
        coordinates.setText(longitude +" "+ latitude);
    }
}
