package com.carbon.restaurantinspection;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Violation;

import java.util.ArrayList;

public class InspectionDetails extends AppCompatActivity {

    private static final String EXTRA_POSITION = "com.carbon.restaurantinspection.InspectionDetails.position";
    private static final String EXTRA_TRACKING_NUMBER = "com.carbon.restaurantinspection.InspectionDetails.trackingNumber";
    private int inspectionPosition;
    private String trackingNumber;
    private InspectionManager inspectionManager = InspectionManager.getInstance(this);
    ArrayList<InspectionDetail> inspectionList = new ArrayList<>();
    private ArrayList<Violation> violationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);

        extractDataFromIntent();
        //delete
        inspectionPosition = 0;
        trackingNumber = "SDFO-8HKP7E";
        updateLists();

        updateViews();
        populateListView();
        registerClickCallBack();
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        inspectionPosition = intent.getIntExtra(EXTRA_POSITION, 0);
        trackingNumber = intent.getStringExtra(EXTRA_TRACKING_NUMBER);
        //adjust arrayList by calling singleton method
    }

    public static Intent makeIntent(Context context, int position, String trackingNumber) {
        Intent intent = new Intent(context, InspectionDetails.class);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_TRACKING_NUMBER, trackingNumber);
        return intent;
    }

    private void updateLists(){
        inspectionList = inspectionManager.getInspections(trackingNumber);
        violationList = inspectionList.get(inspectionPosition).getViolations();
    }

    private void updateViews(){
        TextView textView = findViewById(R.id.inspection_dateText);
        textView.setText("Date: " + inspectionList.get(inspectionPosition).getInspectionDate());

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
        if(hazardLevel.equals("\"High\"")){
            imageView.setImageResource(R.drawable.red_skull_crossbones);
        }
        else if(hazardLevel.equals("\"Moderate\"")){
            imageView.setImageResource(R.drawable.ic_warning_yellow_24dp);
        }
        else{
            imageView.setImageResource(R.drawable.greencheckp);
        }
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.inspection_listView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Violation> {


        public MyListAdapter(){
            super(InspectionDetails.this, R.layout.inspection_item_view, violationList);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.inspection_item_view, parent, false);
            }

            //Find current violation
            Violation currentViolation = violationList.get(position);

            //Fill the violation icon
            ImageView violationView = itemView.findViewById(R.id.item_violationIcon);
            int iconID = findResourceID(currentViolation);
            violationView.setImageResource(iconID);

            //violation title
            TextView titleText = itemView.findViewById(R.id.item_violationTitle);
            titleText.setText(currentViolation.getType() + " Violation");

            //violation title
            TextView descriptionText = itemView.findViewById(R.id.item_violationDescription);
            descriptionText.setText("Code: " + currentViolation.getCode() + ", " + currentViolation.getStatus());

            //Hazard icon
            ImageView hazardView = itemView.findViewById(R.id.item_hazardStatus);
            if (currentViolation.getStatus() == "non critical"){
                hazardView.setImageResource(R.drawable.yellow_caution);
            } else {
                hazardView.setImageResource(R.drawable.red_skull_crossbones);
            }
            return itemView;
        }

        private int findResourceID(Violation currentViolation){
            //6
            int id;
            String type = currentViolation.getType();
            if (type.equals("Permit")){
                id = R.drawable.permit;
            } else if (type.equals("Food")){
                id = R.drawable.food;
            } else if (type.equals("Foodsafe")){
                id = R.drawable.permit;
            } else if (type.equals("Pest")){
                id = R.drawable.pest;
            } else if (type.equals("Hygiene")){
                id = R.drawable.hygiene;
            } else {
                id = R.drawable.equipment;
            }
            return  id;
        }

    }
    private void registerClickCallBack() {
        ListView list = findViewById(R.id.inspection_listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violation clickedViolation = violationList.get(position);
                Toast.makeText(InspectionDetails.this, clickedViolation.getDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
