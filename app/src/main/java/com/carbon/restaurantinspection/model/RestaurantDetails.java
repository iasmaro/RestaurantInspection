package com.carbon.restaurantinspection.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.R;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetails extends AppCompatActivity {
    private int index = 1;
    private RestaurantManager RestManager;
    private InspectionManger InsManager;
    private TextView addy;
    private TextView Latitude;
    private TextView Longitude;
    private List<InspectionDetail> inspections;
    private String trackingNum;
    private Restaurant restaurant;
    private String[] details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestManager = RestaurantManager.getInstance(this);
        InsManager =  InspectionManger.getInstance(this);
        restaurant = RestManager.getRestaurant(index);
        setContentView(R.layout.activity_rest_dets);
        updateAddress();
        getIntents();
        populateStringList();
        displayInspections();
    }

    private void populateStringList() {
        trackingNum = restaurant.getTrackingNumber();
        inspections = InsManager.getInspections(trackingNum);
        int size = inspections.size();
        details = new String[size];
        for(int i = 0;i < size;i++){
            details[i] = inspections.get(i).returnInsDetails();
        }
    }

    private void getIntents() {
        Intent intent = getIntent();
        index = intent.getIntExtra("ca/sfu/restaurantinspections/MainActivity.java:14", 0);
    }

    private void displayInspections() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.items,details);
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

    }
//    private class MyListAdapter extends ArrayAdapter<InspectionDetail>{
//        public MyListAdapter(){
//            super(RestDets.this, R.layout.activity_rest_dets,inspections);
//        }
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent){
//            View itemView = convertView;
//            if(itemView == null){
//                itemView = getLayoutInflater().inflate(R.layout.activity_rest_dets,parent,false);
//            }
//            InspectionDetail currentInspection = inspections.get(position);
//
//
//            return itemView;
//        }
//    }

    private void updateAddress() {
        addy = findViewById(R.id.Address);
        Latitude = findViewById(R.id.Latitude);
        Longitude = findViewById(R.id.Longitude);
        String str = restaurant.getPhysicalAddress();
        addy.setText(str);
        String longitude = Double.toString(restaurant.getLongitude());
        String latitude = Double.toString(restaurant.getLatitude());
        Longitude.setText(longitude);
        Latitude.setText(latitude);
    }
}
