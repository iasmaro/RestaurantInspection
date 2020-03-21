package com.carbon.restaurantinspection.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Violation;

import java.util.ArrayList;

/**
 * Inspection Details Activity contains all details of a specific restaurant's inspection.
 * Data is passed from Restaurant Details via intent encapsulation.
 * Contains a private MyArrayAdapter Class for the listView.
 */

public class InspectionDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_POSITION = "com.carbon.restaurantinspection.userinterface.InspectionDetailsActivity.position";
    private static final String EXTRA_TRACKING_NUMBER = "com.carbon.restaurantinspection.userinterface.InspectionDetailsActivity.trackingNumber";
    private int inspectionPosition;
    private String trackingNumber;
    private InspectionManager inspectionManager;
    ArrayList<InspectionDetail> inspectionList = new ArrayList<>();
    private ArrayList<Violation> violationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inspectionManager = InspectionManager.getInstance(this);
        setContentView(R.layout.activity_inspection_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inspection Details");

        extractDataFromIntent();

        updateLists();

        if (violationList != null){
            updateViews();
        }
        populateListView();
        registerClickCallBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inspection_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
              startActivity(new Intent(this, RestaurantDetailsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        inspectionPosition = intent.getIntExtra(EXTRA_POSITION, 0);
        trackingNumber = intent.getStringExtra(EXTRA_TRACKING_NUMBER);
    }

    public static Intent makeIntent(Context context, int position, String trackingNumber) {
        Intent intent = new Intent(context, InspectionDetailsActivity.class);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_TRACKING_NUMBER, trackingNumber);
        return intent;
    }

    private void registerClickCallBack() {
        ListView list = findViewById(R.id.inspection_listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violation clickedViolation = violationList.get(position);
                Toast.makeText(InspectionDetailsActivity.this, clickedViolation.getDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateLists(){
        inspectionList = inspectionManager.getInspections(trackingNumber);
        violationList = inspectionList.get(inspectionPosition).getViolations();
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.inspection_listView);
        list.setAdapter(adapter);
    }

    private void updateViews(){

        TextView textView = findViewById(R.id.inspection_dateText);
        textView.setText("Date: " + inspectionList.get(inspectionPosition).getFullDate());

        textView = findViewById(R.id.inspection_typeText);
        textView.setText("Type: " + inspectionList.get(inspectionPosition).getInspectionType());

        textView = findViewById(R.id.inspection_criticalText);
        textView.setText("Critical Issues: " + inspectionList.get(inspectionPosition).getNumCritical());

        textView = findViewById(R.id.inspection_nonCriticalText);
        textView.setText("Non-Critical Issues: " + inspectionList.get(inspectionPosition).getNumNonCritical());

        textView = findViewById(R.id.inspection_hazardText);
        String hazardLevel = inspectionList.get(inspectionPosition).getHazardLevel();
        textView.setText("Hazard Level: " + hazardLevel);
        ImageView imageView = findViewById(R.id.inspection_hazardImage);
        if(hazardLevel.equals("High")){
            imageView.setImageResource(R.drawable.red_skull_crossbones);
            textView.setTextColor(Color.RED);
        }
        else if(hazardLevel.equals("Moderate")){
            imageView.setImageResource(R.drawable.ic_warning_yellow_24dp);
            textView.setTextColor(Color.rgb(255,165,0));
        }
        else{
            imageView.setImageResource(R.drawable.greencheckmark);
            textView.setTextColor(Color.GREEN);
        }
    }

    /**
     * MyListAdapter formats the row in the listView to include the necessary data.
     */
    private class MyListAdapter extends ArrayAdapter<Violation> {
        public MyListAdapter(){
            super(InspectionDetailsActivity.this, R.layout.inspection_item_view, violationList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.inspection_item_view, parent, false);
            }

            Violation currentViolation = violationList.get(position);

            ImageView violationView = itemView.findViewById(R.id.item_violationIcon);
            int iconID = findResourceID(currentViolation);
            violationView.setImageResource(iconID);

            TextView titleText = itemView.findViewById(R.id.item_violationTitle);
            titleText.setText(currentViolation.getType() + " Violation");

            TextView descriptionText = itemView.findViewById(R.id.item_violationDescription);
            descriptionText.setText("Code: " + currentViolation.getCode() + ", " + currentViolation.getStatus());

            ImageView hazardView = itemView.findViewById(R.id.item_hazardStatus);
            if (currentViolation.getStatus().equals("Not Critical")){
                hazardView.setImageResource(R.drawable.yellow_caution);
            } else {
                hazardView.setImageResource(R.drawable.red_skull_crossbones);
            }
            return itemView;
        }

        private int findResourceID(Violation currentViolation){
            String type = currentViolation.getType();
            if (type != null) {
                if (type.equals("Permit")){
                    return R.drawable.permit;
                } else if (type.equals("Food")){
                    return R.drawable.food;
                } else if (type.equals("Foodsafe")){
                    return R.drawable.permit;
                } else if (type.equals("Pest")){
                    return R.drawable.pest;
                } else if (type.equals("Hygiene")){
                    return R.drawable.hygiene;
                } else {
                    return R.drawable.equipment;
                }
            } else {
                return 0;
            }
        }
    }
}