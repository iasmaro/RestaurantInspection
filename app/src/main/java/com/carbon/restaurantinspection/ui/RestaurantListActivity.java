package com.carbon.restaurantinspection.ui;

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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;
import java.util.ArrayList;
import java.util.Objects;

/** Displays list of restaurants in alphabetical order, along with icons that represent each icon.
 * For each restaurant, it shows the inspection information which includes
 * the # of issues, hazard level, and latest inspection date.**/
public class RestaurantListActivity extends AppCompatActivity {

    private RestaurantManager restaurantManager;
    private InspectionManager inspectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list_activity);

        restaurantManager = RestaurantManager.getInstance(this);
        inspectionManager = InspectionManager.getInstance(this);

        toolbarBackButton();
        populateRestaurantListView();
        setupClickableRestaurants();
    }

    private void toolbarBackButton() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String activityTitle = getString(R.string.restaurantList);
        Objects.requireNonNull(getSupportActionBar()).setTitle(activityTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantListActivity.this, MapActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void populateRestaurantListView() {
        ArrayAdapter<Restaurant> adapter = new listAdapter();
        ListView list = findViewById(R.id.restaurant_list_view);
        list.setAdapter(adapter);
    }
    private void setupClickableRestaurants() {
        ListView list = findViewById(R.id.restaurant_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = RestaurantDetailsActivity.makeIntent(RestaurantListActivity.this, position);
                startActivity(intent);
            }
        });
    }

    /** Use adapter to fill images and text views **/
    private class listAdapter extends ArrayAdapter<Restaurant> {
        public listAdapter(){
            super(RestaurantListActivity.this, R.layout.display_restaurant_list_activity,
                    restaurantManager.getRestaurantList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.display_restaurant_list_activity,
                        parent,false);
            }

            String restaurantName = restaurantManager.getRestaurant(position).getName();

            setRestaurantNameAndIcon(restaurantName, itemView);

            ArrayList<InspectionDetail> inspections = getInspectionArrayList(position);
            if (inspections != null) {
                inspectionsNotNull(inspections, itemView);
            }
            else {
                inspectionsIsNull(null, itemView);
            }
            return itemView;
        }

        private void inspectionsIsNull(ArrayList<InspectionDetail> inspections, View itemView) {
            String unavailable = getString(R.string.unavailable);

            TextView numIssuesText = itemView.findViewById(R.id.num_issues_textview);
            String numIssuesDisplay = getString(R.string.issues) + " " + unavailable;
            numIssuesText.setText(numIssuesDisplay);
            numIssuesText.setTextColor(ContextCompat.getColor(getContext(), R.color.unavailableColour));

            TextView inspectionDateText = itemView.findViewById(R.id.recent_inspection_date_textview);
            String inspectionDateDisplay = getString(R.string.recentInspectionDate) + " " + unavailable;
            inspectionDateText.setText(inspectionDateDisplay);
            inspectionDateText.setTextColor(ContextCompat.getColor(getContext(), R.color.unavailableColour));

            TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
            String hazardLevelDisplay = getString(R.string.hazardLevel) + " " + unavailable;
            hazardLevelText.setText(hazardLevelDisplay);
            hazardLevelText.setTextColor(ContextCompat.getColor(getContext(), R.color.unavailableColour));

            ImageView hazardLevelIcon = itemView.findViewById(R.id.item_hazard_icon);
            hazardLevelIcon.setImageResource(R.drawable.error_icon);
        }

        private void inspectionsNotNull(ArrayList<InspectionDetail> inspections, View itemView) {
            int numCrit = inspections.get(0).getNumCritical();
            int numNonCrit = inspections.get(0).getNumNonCritical();
            int totalIssues = numCrit + numNonCrit;

            TextView numIssuesText = itemView.findViewById(R.id.num_issues_textview);
            String numIssuesDisplay = getString(R.string.issues);
            numIssuesText.setText(numIssuesDisplay + " " + totalIssues);
            numIssuesText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            String date = inspections.get(0).getInspectionDate();
            TextView dateText = itemView.findViewById(R.id.recent_inspection_date_textview);
            String recentInspectionDisplay = getString(R.string.recentInspectionDate);
            dateText.setText(recentInspectionDisplay + " " + date);
            dateText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            String hazardLevel = inspections.get(0).getHazardLevel();
            TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
            String hazardLevelDisplay = getString(R.string.hazardLevel);
            hazardLevelText.setText(hazardLevelDisplay + " " + hazardLevel);

            if (hazardLevel.contains("Low")){
                hazardLevelText.setTextColor(ContextCompat.getColor(getContext(), R.color.lowCriticalColour));
            }
            else if (hazardLevel.contains("Moderate")) {
                hazardLevelText.setTextColor(ContextCompat.getColor(getContext(), R.color.moderateCriticalColour));
            }
            else {
                hazardLevelText.setTextColor(ContextCompat.getColor(getContext(), R.color.highCriticalColour));
            }

            setHazardLevelIcons(hazardLevel, itemView);
        }

        private void setHazardLevelIcons(String hazardLevel, View itemView) {
            ImageView hazardLevelIcon = itemView.findViewById(R.id.item_hazard_icon);
            if (hazardLevel.contains("Low")){
                hazardLevelIcon.setImageResource(R.drawable.greencheckmark);
            }
            else if (hazardLevel.contains("Moderate"))
            {
                hazardLevelIcon.setImageResource(R.drawable.yellow_caution);
            }
            else {
                hazardLevelIcon.setImageResource(R.drawable.red_skull_crossbones);
            }
        }

        private void setRestaurantNameAndIcon(String restaurantName, View itemView) {
            TextView restaurantNameText = itemView.findViewById(R.id.restaurant_name_textview);
            restaurantNameText.setText(restaurantName);

            ImageView restaurantIcon = itemView.findViewById(R.id.item_restaurant_icon);
            if (restaurantName.contains("A&W")){
                restaurantIcon.setImageResource(R.drawable.a_w_restaurant_icon);
            }
            else if(restaurantName.contains("Blenz Coffee")) {
                restaurantIcon.setImageResource(R.drawable.blenz_icon);
            }
            else if(restaurantName.contains("McDonald's")) {
                restaurantIcon.setImageResource(R.drawable.macdonald_icon);
            }
            else if(restaurantName.contains("Starbucks")) {
                restaurantIcon.setImageResource(R.drawable.starbucks_icon);
            }
            else if(restaurantName.contains("Tim Hortons")) {
                restaurantIcon.setImageResource(R.drawable.timhortons_icon);
            }
            else if(restaurantName.contains("Wendy's")) {
                restaurantIcon.setImageResource(R.drawable.wendys_icon);
            }
            else if(restaurantName.contains("Burger King")) {
                restaurantIcon.setImageResource(R.drawable.burgerking_icon);
            }
            else if(restaurantName.contains("Pizza Hut")) {
                restaurantIcon.setImageResource(R.drawable.pizzahut_icon);
            }
            else if(restaurantName.contains("Domino's")) {
                restaurantIcon.setImageResource(R.drawable.dominos_icon);
            }
            else if(restaurantName.contains("7-Eleven")) {
                restaurantIcon.setImageResource(R.drawable.seveneleven_icon);
            }
            else {
                restaurantIcon.setImageResource(R.drawable.store_icon);
            }
        }

        private ArrayList<InspectionDetail> getInspectionArrayList(int position) {
            String restaurantTrackingNum = restaurantManager.getRestaurant(position).getTrackingNumber();
            String trackNum = restaurantTrackingNum;
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(trackNum);
            return inspections;
        }
    }
}
