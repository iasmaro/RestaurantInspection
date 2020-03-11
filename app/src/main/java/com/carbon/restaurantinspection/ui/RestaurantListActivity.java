package com.carbon.restaurantinspection.ui;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {

    ArrayList<Integer> numIssues = new ArrayList<Integer>();

    // calls RestaurantManager class
    private RestaurantManager restaurantManager;
    private InspectionManager inspectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list_activity);

        // Get that one instance that RestaurantManager class produced
        restaurantManager = RestaurantManager.getInstance(this);
        inspectionManager = InspectionManager.getInstance(this);

        populateRestaurantListView();
    }

    private void populateRestaurantListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
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
            String currentRestaurants = restaurantManager.getRestaurant(position).getName().replace("\"", "");

            //fill in the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_restaurant_icon);
            if (currentRestaurants.contains("A&W")){
                imageView.setImageResource(R.drawable.a_w_restaurant_icon);
            } else
                imageView.setImageResource(R.drawable.beer_icon);

           String restaurantTrackingNum = restaurantManager.getRestaurant(position).getTrackingNumber();
           String restarantTrackNum = restaurantTrackingNum.replace("\"", "");

           ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(restarantTrackNum);
//           Collections.sort(inspections);

            // gets inspection date and already compares current date with inspection date

            // set # issues
            if(inspections != null) {
                int numCrit = inspections.get(0).getNumCritical();
                int numNonCrit = inspections.get(0).getNumNonCritical();
                int totalIssues = numCrit + numNonCrit;

                TextView numIssuesText = itemView.findViewById(R.id.num_issues_textview);
                numIssuesText.setText("# Issues: " + totalIssues);
                //Log.d("total issues is : ", Integer.toString(totalIssues));

//                Calendar dateFormatDispay = inspections.get(0).getInspecDate();
//                int daysBetween = inspections.get(0).daysBetween(dateFormatDispay);
//                Log.d("date is : ", Integer.toString(daysBetween));

//                TextView inspectionDateText = itemView.findViewById(R.id.recent_inspection_date_textview);
//                inspectionDateText.setText("Recent inspection: " + date);

                //get recent inspection date
                String date = inspections.get(0).getInspectionDate();
                TextView recentInspectionDateText = itemView.findViewById(R.id.recent_inspection_date_textview);
                recentInspectionDateText.setText("" + date);

                //get hazard level
                String hazardLevel = inspections.get(0).getHazardLevel();
                TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
                hazardLevelText.setText("Hazard Level: " + hazardLevel);

                //fill in the view for hazard level
                ImageView imageViewHazardLevel = itemView.findViewById(R.id.item_hazard_icon);
                if (hazardLevel.contains("Low")){
                    imageViewHazardLevel.setImageResource(R.drawable.low_hazard);
                }
                if (hazardLevel.contains("Moderate"))
                {
                    imageViewHazardLevel.setImageResource(R.drawable.moderate_hazard);
                }
                if (hazardLevel.contains("High"))
                {
                    imageViewHazardLevel.setImageResource(R.drawable.high_hazard);
                }
            }
            else {

                // # issues
                TextView numIssuesText = (TextView) itemView.findViewById(R.id.num_issues_textview);
                numIssuesText.setText("# Issues: " + 0);

                // hazard level
                TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
                hazardLevelText.setText("Unavailable Hazard Level");

                // icon for hazard level
                ImageView imageViewHazardLevel = itemView.findViewById(R.id.item_hazard_icon);
                imageViewHazardLevel.setImageResource(R.drawable.error_icon);

                TextView inspectionDateText = itemView.findViewById(R.id.recent_inspection_date_textview);
                inspectionDateText.setText("Recent Inspection Unavailable");
            }

            // set restaurant name
           TextView restaurantNameText = (TextView) itemView.findViewById(R.id.restaurant_name_textview);
           restaurantNameText.setText(currentRestaurants);

            // clickable listview
            ListView list = findViewById(R.id.restaurant_list_view);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id){

                    Toast.makeText(RestaurantListActivity.this, "Position: " + position, Toast.LENGTH_LONG).show();
                }
            });
            return itemView;
        }
    }

}
