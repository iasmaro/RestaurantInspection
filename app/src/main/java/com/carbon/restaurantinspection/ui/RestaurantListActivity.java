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
import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;
import java.util.ArrayList;

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

        populateRestaurantListView();
        setupClickableRestaurants();
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

            String restaurantName = restaurantManager.getRestaurant(position).getName()
                    .replace("\"", "");

            setRestaurantNameAndIcon(restaurantName, itemView);

            ArrayList<InspectionDetail> inspections = getInspectionArrayList(position);
            if (inspections != null) {
                inspectionsNotNull(inspections, itemView);
            }
            else {
                inspectionsIsNull(inspections, itemView);
            }
            return itemView;
        }

        private void inspectionsIsNull(ArrayList<InspectionDetail> inspections, View itemView) {
            String unavailable = "Unavailable";

            TextView numIssuesText = itemView.findViewById(R.id.num_issues_textview);
            String numIssuesDisplay = "              " + "# Issues: " + unavailable;
            numIssuesText.setText(numIssuesDisplay);

            TextView inspectionDateText = itemView.findViewById(R.id.recent_inspection_date_textview);
            String inspectionDateContent = "Recent Inspection Date: " + unavailable;
            inspectionDateText.setText(inspectionDateContent);

            TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
            String hazardLevelDisplay = "Hazard Level: " + unavailable;
            hazardLevelText.setText(hazardLevelDisplay);

            ImageView hazardLevelIcon = itemView.findViewById(R.id.item_hazard_icon);
            hazardLevelIcon.setImageResource(R.drawable.error_icon);
        }

        private void inspectionsNotNull(ArrayList<InspectionDetail> inspections, View itemView) {
            int numCrit = inspections.get(0).getNumCritical();
            int numNonCrit = inspections.get(0).getNumNonCritical();
            int totalIssues = numCrit + numNonCrit;

            TextView numIssuesText = itemView.findViewById(R.id.num_issues_textview);
            String numIssuesDisplay = "# Issues: " + totalIssues;
            numIssuesText.setText(numIssuesDisplay);

            String date = inspections.get(0).getInspectionDate();
            TextView dateText = itemView.findViewById(R.id.recent_inspection_date_textview);
            String dateDisplay = "Recent Inspection Date: " + date;
            dateText.setText(dateDisplay);

            String hazardLevel = inspections.get(0).getHazardLevel();
            TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
            String hazardLevelDisplay = "Hazard Level: " + hazardLevel;
            hazardLevelText.setText(hazardLevelDisplay);

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
            else if(restaurantName.contains("Seafood")) {
                restaurantIcon.setImageResource(R.drawable.seafood_icon);
            }
            else if(restaurantName.contains("Sushi")) {
                restaurantIcon.setImageResource(R.drawable.sushi_icon);
            }
            else if(restaurantName.contains("Pizza")) {
                restaurantIcon.setImageResource(R.drawable.pizza_icon);
            }
            else if(restaurantName.contains("Chicken")) {
                restaurantIcon.setImageResource(R.drawable.chicken_icon);
            }
            else {
                restaurantIcon.setImageResource(R.drawable.beer_icon);
            }
        }

        private ArrayList<InspectionDetail> getInspectionArrayList(int position) {
            String restaurantTrackingNum = restaurantManager.getRestaurant(position).getTrackingNumber();
            String trackNum = restaurantTrackingNum.replace("\"", "");
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(trackNum);
            return inspections;
        }
    }
}
