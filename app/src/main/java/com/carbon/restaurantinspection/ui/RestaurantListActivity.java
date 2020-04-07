package com.carbon.restaurantinspection.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.carbon.restaurantinspection.model.Favourites.getFavouriteList;
import static com.carbon.restaurantinspection.model.Favourites.isRestaurantInFavourites;

/** Displays list of restaurants in alphabetical order, along with icons that represent each icon.
 * For each restaurant, it shows the inspection information which includes
 * the # of issues, hazard level, and latest inspection date.**/
public class RestaurantListActivity extends AppCompatActivity {

    public static final String NUM_OF_CRIT = "com.carbon.restaurantinspection.ui.filterFragment.numOfCrit";
    public static final String HAZARD_LEVEL = "com.carbon.restaurantinspection.ui.filterFragment.hazardLevel";
    public static final String FAVOURITE_CHECKED = "com.carbon.restaurantinspection.ui.filterFragment.favouriteChecked";
    public static final String GREATER_CHECKED = "com.carbon.restaurantinspection.ui.filterFragment.greaterChecked";
    private RestaurantManager restaurantManager;
    private InspectionManager inspectionManager;
    private Toolbar toolbar;
    private static int FILTER_REQUEST_CODE = 555;
    ArrayAdapter<String> arrayAdapter;
    private int numOfCriticalVioaltionsfromFilter;
    private String hazardLevelFromFilter;
    private boolean favouritesChecked;
    private boolean greaterChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list_activity);

        restaurantManager = RestaurantManager.getInstance(this);
        inspectionManager = InspectionManager.getInstance(this);
        toolbar = findViewById(R.id.toolbar);

        toolbarSetUp();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        populateRestaurantListView();
        setupClickableRestaurants();
        if (favouritesChecked || hazardLevelFromFilter != null
                || numOfCriticalVioaltionsfromFilter > 0) {
            filterRestaurants();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.map_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        String searchHere = getString(R.string.searchHere);
        searchView.setQueryHint(searchHere);
        if (restaurantManager.getSearchTerm() != null) {
            searchView.setQuery(restaurantManager.getSearchTerm(), false);
            searchView.setIconified(false);
            searchView.clearFocus();
        }
        searchView.setQueryHint(searchHere);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.isEmpty()) {
                    ArrayList<Restaurant> searched = restaurantManager.searchRestaurants(s);
                    if (searched.isEmpty()) {
                        ListView list = findViewById(R.id.restaurant_list_view);
                        list.setAdapter(null);
                    } else {
                        populateRestaurantListView();
                        setupClickableRestaurants();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //arrayAdapter.getFilter().filter(s);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                restaurantManager.clearSearch();
                populateRestaurantListView();
                setupClickableRestaurants();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.filter){
                    Intent intent = new Intent(RestaurantListActivity.this, FilterFragment.class);
                    startActivityForResult(intent, FILTER_REQUEST_CODE);
                    return true;
        } else if (item.getItemId() == R.id.clear_text_icon) {
            restaurantManager.clearFilter();
            populateRestaurantListView();
            setupClickableRestaurants();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                numOfCriticalVioaltionsfromFilter = data.getIntExtra(
                        NUM_OF_CRIT, 0);
                hazardLevelFromFilter = data.getStringExtra(
                        HAZARD_LEVEL);
                favouritesChecked = data.getBooleanExtra(
                        FAVOURITE_CHECKED,
                        false);
                greaterChecked = data.getBooleanExtra(GREATER_CHECKED, false);
            }
        }
    }

    private void filterRestaurants() {
        ArrayList<String> filtered;
        if (favouritesChecked && hazardLevelFromFilter != null
                && numOfCriticalVioaltionsfromFilter > 0) {
            filtered = inspectionManager.filter(getFavouriteList(),
                    hazardLevelFromFilter, numOfCriticalVioaltionsfromFilter, greaterChecked);
        } else if (favouritesChecked && hazardLevelFromFilter != null) {
            filtered = inspectionManager.filter(getFavouriteList(),
                    hazardLevelFromFilter);
        } else if (hazardLevelFromFilter != null && numOfCriticalVioaltionsfromFilter > 0) {
            filtered = inspectionManager.filter(hazardLevelFromFilter,
                    numOfCriticalVioaltionsfromFilter, greaterChecked);

        } else if (favouritesChecked && numOfCriticalVioaltionsfromFilter > 0) {
            filtered = inspectionManager.filter(getFavouriteList(),
                    numOfCriticalVioaltionsfromFilter, greaterChecked);
        } else if (favouritesChecked) {
            filtered = getFavouriteList();
        } else if (hazardLevelFromFilter != null) {
            filtered = inspectionManager.filter(hazardLevelFromFilter);
        } else if (numOfCriticalVioaltionsfromFilter > 0) {
            filtered = inspectionManager.filter(numOfCriticalVioaltionsfromFilter, greaterChecked);
        } else {
            filtered = new ArrayList<>();
        }
        ArrayList<Restaurant> filteredList = restaurantManager.filter(filtered);
        if (filteredList.isEmpty()) {
            ListView list = findViewById(R.id.restaurant_list_view);
            list.setAdapter(null);
        } else {
            populateRestaurantListView();
            setupClickableRestaurants();
        }
    }

    private void toolbarSetUp() {
        toolbarBackButton();
        List<Restaurant> restaurantList = restaurantManager.getRestaurantList();
        int size = restaurantList.size();

        List<String> restaurantNames = new ArrayList<>();
        for(int i = 0; i < size; i++){
            String name = restaurantList.get(i).getName();
            restaurantNames.add(name);
        }
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                restaurantNames);
    }

    private void toolbarBackButton() {
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
        ArrayAdapter<Restaurant> adapter = new listAdapter(restaurantManager.getRestaurantList());
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
        public listAdapter(ArrayList<Restaurant> restaurants){
            super(RestaurantListActivity.this, R.layout.display_restaurant_list_activity,
                    restaurants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.display_restaurant_list_activity,
                        parent,false);
            }

            String restaurantName = restaurantManager.getRestaurant(position).getName();
            String trackingNumber = restaurantManager.getRestaurant(position).getTrackingNumber();

            setRestaurantNameAndIcon(restaurantName, itemView);
            setBackground(trackingNumber, itemView);

            ArrayList<InspectionDetail> inspections = getInspectionArrayList(position);
            if (inspections != null) {
                inspectionsNotNull(inspections, itemView);
            }
            else {
                inspectionsIsNull(null, itemView);
            }
            return itemView;
        }

        private void setBackground(String trackingNumber, View itemView) {
            ConstraintLayout layout = itemView.findViewById(R.id.restaurant_list_layout);
            if (isRestaurantInFavourites(trackingNumber)) {
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.red_hearts, null);
                drawable.setAlpha(70);
                layout.setBackground(drawable);
            }
            else {
                layout.setBackgroundResource(0);
            }
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

            String date = inspections.get(0).getInspectionDate(RestaurantListActivity.this);
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