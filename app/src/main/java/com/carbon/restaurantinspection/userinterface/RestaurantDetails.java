package com.carbon.restaurantinspection.userinterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

import java.util.ArrayList;
import java.util.List;

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
    private static RestaurantDetails instance;
    private InspectionManager InsManager;
    private List<myInspections> inspectionList = new ArrayList<>();
    private Restaurant restaurant;
    ArrayAdapter<myInspections> adapter;
    private String trackingNum;


    public static RestaurantDetails getInstance(Context context) {
        if (instance == null) {
            instance = new RestaurantDetails();
        }
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntents();
        RestaurantManager restManager = RestaurantManager.getInstance(this);
        InsManager =  InspectionManager.getInstance(this);
        restaurant = restManager.getRestaurant(index);
        setContentView(R.layout.activity_rest_dets);
        updateAddress();
        populateStringList();
        populateListView();
        onInspectionClick();
    }

    private void onInspectionClick() {
        ListView listView = findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = InspectionDetailsActivity.makeIntent(RestaurantDetails.this,index
                        ,trackingNum);
                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        adapter = new MyListAdapter();
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    private void populateStringList() {
        trackingNum = restaurant.getTrackingNumber();
        List<InspectionDetail> inspections = InsManager.getInspections(trackingNum);
        int size = inspections.size();
        myInspections[] ins = new myInspections[size];
        for(int i = 0;i < size;i++){
            String str = inspections.get(i).returnInsDetails();
            ins[i] = new myInspections();
            ins[i].setDetails(str);
            String string = inspections.get(i).getHazardLevel();
            if(string.equals("\"High\"")){
                ins[i].setIconId(R.drawable.red_skull_crossbones);
            }
            else if(string.equals("\"Moderate\"")){
                ins[i].setIconId(R.drawable.ic_warning_yellow_24dp);
            }
            else
                ins[i].setIconId(R.drawable.greencheckp);
            inspectionList.add(ins[i]);
        }
    }

    private void getIntents() {
        Intent intent = getIntent();
        index = intent.getIntExtra("com/carbon/restaurantinspection/model/MainActivity.java:30"
                , 0);
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

//// Put the tracking number and index into an intent and pass to Inspection detail.