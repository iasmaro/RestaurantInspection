package com.carbon.restaurantinspection.ui;

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

    /** fills list of restaurants in list view**/
    private void populateRestaurantListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.restaurant_list_view);
        list.setAdapter(adapter);
    }
    private void setupClickableRestaurants() {
        ListView list = findViewById(R.id.restaurant_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
//                Intent intent = RestaurantDetailsActivity.makeIntent(RestaurantListActivity.this, position);
//                startActivity(intent);
            }
        });
    }

    /** Use adapter to fill images and text views **/
    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter(){
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

            String currentRestaurants = restaurantManager.getRestaurant(position).getName()
                    .replace("\"", "");

            ImageView imageView = itemView.findViewById(R.id.item_restaurant_icon);
            if (currentRestaurants.contains("A&W")){
                imageView.setImageResource(R.drawable.a_w_restaurant_icon);
            }
            else if(currentRestaurants.contains("Seafood")) {
                imageView.setImageResource(R.drawable.seafood_icon);
            }
            else if(currentRestaurants.contains("Sushi")) {
                imageView.setImageResource(R.drawable.sushi_icon);
            }
            else if(currentRestaurants.contains("Pizza")) {
                imageView.setImageResource(R.drawable.pizza_icon);
            }
            else if(currentRestaurants.contains("Chicken")) {
                imageView.setImageResource(R.drawable.chicken_icon);
            }
            else {
                imageView.setImageResource(R.drawable.beer_icon);
            }

            TextView restaurantNameText = itemView.findViewById(R.id.restaurant_name_textview);
            restaurantNameText.setText(currentRestaurants);

            /**find inspections of a specific restaurant**/
            String restaurantTrackingNum = restaurantManager.getRestaurant(position).getTrackingNumber();
            String restarantTrackNum = restaurantTrackingNum.replace("\"", "");
            ArrayList<InspectionDetail> inspections = inspectionManager.getInspections(restarantTrackNum);

            if(inspections != null) {
                int numCrit = inspections.get(0).getNumCritical();
                int numNonCrit = inspections.get(0).getNumNonCritical();
                int totalIssues = numCrit + numNonCrit;

                TextView numIssuesText = itemView.findViewById(R.id.num_issues_textview);
                String numIssuesTextContent = "# Issues: " + totalIssues;
                numIssuesText.setText(numIssuesTextContent);

                String date = inspections.get(0).getInspectionDate();
                TextView recentInspectionDateText = itemView.findViewById(R.id.recent_inspection_date_textview);
                String recentInspectionDateTextContent = "Recent Inspection Date: " + date;
                recentInspectionDateText.setText(recentInspectionDateTextContent);

                String hazardLevel = inspections.get(0).getHazardLevel();
                TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
                String hazardlevelTextContent = "Hazard Level: " + hazardLevel;
                hazardLevelText.setText(hazardlevelTextContent);

                ImageView imageViewHazardLevel = itemView.findViewById(R.id.item_hazard_icon);
                if (hazardLevel.contains("Low")){
                    imageViewHazardLevel.setImageResource(R.drawable.low_hazard);
                }
                else if (hazardLevel.contains("Moderate"))
                {
                    imageViewHazardLevel.setImageResource(R.drawable.yellow_caution);
                }
                else {
                    imageViewHazardLevel.setImageResource(R.drawable.red_skull_crossbones);
                }
            }

            /** displays the correct headings when inspection array list is null **/
            else {
                TextView numIssuesText = itemView.findViewById(R.id.num_issues_textview);
                String numIssuesContent = "              " + "# Issues: Unavailable";
                numIssuesText.setText(numIssuesContent);

                TextView inspectionDateText = itemView.findViewById(R.id.recent_inspection_date_textview);
                String inspectionDateContent = "Recent Inspection Date: Unavailable";
                inspectionDateText.setText(inspectionDateContent);

                TextView hazardLevelText = itemView.findViewById(R.id.hazard_level_textview);
                String hazardLevelContent = "Hazard Level: Unavailable";
                hazardLevelText.setText(hazardLevelContent);

                ImageView imageViewHazardLevel = itemView.findViewById(R.id.item_hazard_icon);
                imageViewHazardLevel.setImageResource(R.drawable.error_icon);
            }
            return itemView;
        }
    }
}
