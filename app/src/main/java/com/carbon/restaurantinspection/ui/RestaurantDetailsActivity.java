package com.carbon.restaurantinspection.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;

import java.util.ArrayList;
import java.util.List;

import static com.carbon.restaurantinspection.model.Favourites.addRestaurantToFavourites;
import static com.carbon.restaurantinspection.model.Favourites.isRestaurantInFavourites;
import static com.carbon.restaurantinspection.model.Favourites.removeRestaurantToFavourites;

/**
 * Class makes it easier to display an icon beside the inspections
 */
class InspectionDetailHolder {
    private InspectionDetail inspectionDetail;
    private int iconId;

    InspectionDetailHolder(int id, InspectionDetail detail) {
        iconId = id;
        inspectionDetail = detail;
    }
    int getIconId() {
        return iconId;
    }

    String getDate() {
        return inspectionDetail.getInspectionDate();
    }

    int getNumCritical(){
        return inspectionDetail.getNumCritical();
    }

    int getNumNonCritical(){
        return inspectionDetail.getNumNonCritical();
    }
}

/**
 *  Displays a list of Restaurants, their number of inspections(critical and non-critical), sorted
 *  by date from most recent inspection to oldest.
 */
public class RestaurantDetailsActivity extends AppCompatActivity {
    public static final String INTENT_NAME = "com/carbon/restaurantinspection/model/MainActivity.java:30";
    public static final String TAG = "RestaurantDetailsActivity";
    private int index;
    private InspectionManager myInspectionManager;
    private List<InspectionDetailHolder> inspectionList = new ArrayList<>();
    private Restaurant restaurant;
    private String trackingNum;
    List<InspectionDetail> inspections;
    public static double longitude = 0;
    public static double latitude = 0;
//    private ArrayList<String> favouriteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        getIntents();
        RestaurantManager restManager = RestaurantManager.getInstance(this);
        myInspectionManager =  InspectionManager.getInstance(this);
        restaurant = restManager.getRestaurant(index);

        toolbarBackButton();
        clickCoordsToMap();
        updateAddress();
        populateStringList();
        populateListView();
        onInspectionClick();

        setUpCheckBox();
        setUpCheckBoxClick();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, RestaurantListActivity.class));
    }

    private void setUpCheckBox() {
        final Switch favouriteSwitch = findViewById(R.id.favouriteSwitch);
        if(isRestaurantInFavourites(trackingNum)){
            favouriteSwitch.setChecked(true);
        } else {
            favouriteSwitch.setChecked(false);
        }
    }

    private void setUpCheckBoxClick() {
        final Switch favouriteSwitch = findViewById(R.id.favouriteSwitch);
        favouriteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    addRestaurantToFavourites(trackingNum);
                    favouriteSwitch.setChecked(true);
                } else {
                    removeRestaurantToFavourites(trackingNum);
                    favouriteSwitch.setChecked(false);
                }
            }
        });
    }

    private void clickCoordsToMap() {
        TextView coordinates = findViewById(R.id.Coordinates);
        coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MapActivity.makeIntent(RestaurantDetailsActivity.this, index);
                startActivity(intent);
                finish();
            }
        });
    }

    private void toolbarBackButton() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(restaurant.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.back) {
            startActivity(new Intent(this, RestaurantListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(INTENT_NAME, index);
        return intent;
    }

    private void onInspectionClick() {
        if(inspections != null) {
            ListView listView = findViewById(R.id.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = InspectionDetailsActivity.makeIntent(RestaurantDetailsActivity.this,
                            i, trackingNum);

                    startActivity(intent);
                }
            });
        }
    }

    private void populateStringList() {
        trackingNum = restaurant.getTrackingNumber();
        inspections = myInspectionManager.getInspections(trackingNum);
        if(inspections != null) {
            int size = inspections.size();

            for (int i = 0; i < size; i++) {
                int iconId;
                String hazardLevel = inspections.get(i).getHazardLevel();

                if (hazardLevel.equals("High")) {
                    iconId = R.drawable.red_skull_crossbones;
                } else if (hazardLevel.equals("Moderate")) {
                    iconId = R.drawable.ic_warning_yellow_24dp;
                } else {
                    iconId = R.drawable.greencheckmark;
                }
                InspectionDetailHolder inspectionDetailHolder = new InspectionDetailHolder(iconId
                        ,inspections.get(i));
                inspectionList.add(inspectionDetailHolder);
            }
        }
        else{
            TextView textView = findViewById(R.id.inspectionTitle);
            textView.setText(R.string.ifNoInspection);
        }
    }

    private void populateListView() {
        ArrayAdapter<InspectionDetailHolder> adapter;
        adapter = new MyListAdapter();
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    private void updateAddress() {
        TextView address = findViewById(R.id.Address);
        TextView latitude1 = findViewById(R.id.Coordinates);
        String str = restaurant.getPhysicalAddress();
        address.setText("Address: " + str);
        String longitude = Double.toString(restaurant.getLongitude());
        String latitude = Double.toString(restaurant.getLatitude());
        latitude1.setText("Coordinates: " + longitude + ",  " + latitude);
    }

    private void getIntents() {
        Intent intent = getIntent();
        index = intent.getIntExtra(INTENT_NAME, 0);
    }

    /**
     * MyListAdapter was formatted to be able to display the necessary information in the listView
     */
    private class MyListAdapter extends ArrayAdapter<InspectionDetailHolder>{
        MyListAdapter(){
            super(RestaurantDetailsActivity.this,R.layout.item_view, inspectionList);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            if (inspections == null) {
                TextView makeText = itemView.findViewById(R.id.Text);
                makeText.setText(R.string.ifNoInspection);
            }

            else {
                InspectionDetailHolder currentInspection = inspectionList.get(position);

                ImageView imageView = itemView.findViewById(R.id.icon);
                imageView.setImageResource(currentInspection.getIconId());

                TextView makeText = itemView.findViewById(R.id.Text);
                makeText.setText(currentInspection.getDate());

                TextView makeText2 = itemView.findViewById(R.id.numCritical);
                String numCrit = Integer.toString(currentInspection.getNumCritical());
                makeText2.setText("Critical issues: "+ numCrit);

                String numNonCrit = Integer.toString(currentInspection.getNumNonCritical());
                TextView makeText3 = itemView.findViewById(R.id.numNonCritical);
                makeText3.setText("Non-critical issues: "+ numNonCrit);

            }
            return itemView;
        }
    }
}
