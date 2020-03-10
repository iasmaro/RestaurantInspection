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

import com.carbon.restaurantinspection.InspectionDetails;
import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManger;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

import java.util.ArrayList;
import java.util.List;


// Class to help me display my an icon beside the the inspections
class InspectionDetailHolder {
    private String details;
    private int iconId;

    InspectionDetailHolder(String str, int id) {
        details = str;
        iconId = id;
    }

    void setDetails(String details) {
        this.details = details;
    }

    void setIconId(int iconId) {
        this.iconId = iconId;
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
    private InspectionManger InsManager;
    private List<InspectionDetailHolder> inspectionList = new ArrayList<>();
    private Restaurant restaurant;
    ArrayAdapter<InspectionDetailHolder> adapter;
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
        InsManager =  InspectionManger.getInstance(this);
        restaurant = restManager.getRestaurant(index);
        setContentView(R.layout.activity_rest_dets);
        updateAddress();
        populateStringList();
        populateListView();
     //   onInspectionClick();
    }

//    private void onInspectionClick() {
//        ListView listView = findViewById(R.id.list);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = InspectionDetails.makeIntent(RestaurantDetails.this,index
//                        ,trackingNum);
//                startActivity(intent);
//            }
//        });
//    }



    private void populateStringList() {
        trackingNum = restaurant.getTrackingNumber();
        List<InspectionDetail> inspections = InsManager.getInspections(trackingNum);
        int size = inspections.size();
        InspectionDetailHolder[] ins = new InspectionDetailHolder[size];

        for(int i = 0;i < size;i++){
            String str = inspections.get(i).returnInsDetails();
            int iconId;
            String hazardLevel = inspections.get(i).getHazardLevel();

            if(hazardLevel.equals("\"High\"")){
                iconId = R.drawable.red_skull_crossbones;
            }
            else if(hazardLevel.equals("\"Moderate\"")){
                iconId = R.drawable.ic_warning_yellow_24dp;
            }
            else
                iconId = R.drawable.greencheckmark;
            ins[i] = new InspectionDetailHolder(str, iconId);
            inspectionList.add(ins[i]);
        }
    }

    private class MyListAdapter extends ArrayAdapter<InspectionDetailHolder>{
        MyListAdapter(){
            super(RestaurantDetails.this,R.layout.item_view, inspectionList);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }
            InspectionDetailHolder currentInspection = inspectionList.get(position);

            ImageView imageView = itemView.findViewById(R.id.icon);
            imageView.setImageResource(currentInspection.getIconId());

            TextView makeText = itemView.findViewById(R.id.Text);
            makeText.setText(currentInspection.getDetails());

            return itemView;
        }
    }

    private void populateListView() {
        adapter = new MyListAdapter();
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    private void updateAddress() {
        TextView address = findViewById(R.id.Address);
        TextView latitude1 = findViewById(R.id.Latitude);
        TextView longitude1 = findViewById(R.id.Longitude);
        String str = restaurant.getPhysicalAddress();
        address.setText(str);
        String longitude = Double.toString(restaurant.getLongitude());
        String latitude = Double.toString(restaurant.getLatitude());
        longitude1.setText(longitude);
        latitude1.setText(latitude);
    }

    private void getIntents() {
        Intent intent = getIntent();
        index = intent.getIntExtra("com/carbon/restaurantinspection/model/MainActivity.java:30"
                , 0);
    }
}