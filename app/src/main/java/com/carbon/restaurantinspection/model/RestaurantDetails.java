package com.carbon.restaurantinspection.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class myInspections{
    private String details;
    private int iconId;

    void setDetails(String details) {
        this.details = details;
    }

    void setIconId(int iconId) {
        this.iconId = iconId;
    }

    myInspections() {
        details = ".";
        iconId = 0;
    }

    int getIconId() {
        return iconId;
    }

    String getDetails() {
        return details;
    }
}
public class RestaurantDetails extends AppCompatActivity {
    private int index;
    private InspectionManger InsManager;
    private List<myInspections> inspectionList = new ArrayList<>();
    private Restaurant restaurant;
    ArrayAdapter<myInspections> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestaurantManager restManager = RestaurantManager.getInstance(this);
        InsManager =  InspectionManger.getInstance(this);
        restaurant = restManager.getRestaurant(index);
        setContentView(R.layout.activity_rest_dets);
        updateAddress();
        getIntents();
        populateStringList();
        populateListView();

    }

    private void populateListView() {
        adapter = new MyListAdapter();
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    private void populateStringList() {
        String trackingNum = restaurant.getTrackingNumber();
        List<InspectionDetail> inspections = InsManager.getInspections(trackingNum);
        int size = inspections.size();
        myInspections[] ins = new myInspections[size];
        for(int i = 0;i < size;i++){
            String str = inspections.get(i).returnInsDetails();
            ins[i] = new myInspections();
            ins[i].setDetails(str);
            String string = inspections.get(i).getHazardLevel();
            if(Objects.equals(string, "High")){
                ins[i].setIconId(R.drawable.ic_warning_red_24dp);
            }
            else{
                ins[i].setIconId(R.drawable.ic_warning_yellow_24dp);
            }
            inspectionList.add(ins[i]);
        }
    }

    private void getIntents() {
        Intent intent = getIntent();
        index = intent.getIntExtra("ca/sfu/restaurantinspections/MainActivity.java:14", 0);
    }


    private class MyListAdapter extends ArrayAdapter<myInspections>{
        MyListAdapter(){
            super(RestaurantDetails.this,R.layout.item_view, inspectionList);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }
            myInspections currentInspection = inspectionList.get(position);

            ImageView imageView = itemView.findViewById(R.id.icon);
            imageView.setImageResource(currentInspection.getIconId());

            TextView makeText = itemView.findViewById(R.id.Text);
            makeText.setText(currentInspection.getDetails());

            return itemView;
        }
    }

    private void updateAddress() {
        TextView addy = findViewById(R.id.Address);
        TextView latitude1 = findViewById(R.id.Latitude);
        TextView longitude1 = findViewById(R.id.Longitude);
        String str = restaurant.getPhysicalAddress();
        addy.setText(str);
        String longitude = Double.toString(restaurant.getLongitude());
        String latitude = Double.toString(restaurant.getLatitude());
        longitude1.setText(longitude);
        latitude1.setText(latitude);
    }
}

// Put the tracking number and index into an intent and pass to Inspection detail.