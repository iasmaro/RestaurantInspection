package com.carbon.restaurantinspection.model;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.R;

public class RestDets extends AppCompatActivity {
    private int index = 1;
    private RestaurantManager RestManager = RestaurantManager.getInstance(this);
    private InspectionManger InsManager = InspectionManger.getInstance(this);
    TextView addy;
    TextView coordinates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
