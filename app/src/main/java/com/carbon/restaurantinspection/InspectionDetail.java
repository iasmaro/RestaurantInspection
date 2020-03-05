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

import java.util.ArrayList;

public class InspectionDetail extends AppCompatActivity {

    private static final String EXTRA_POSITION = "com.carbon.restaurantinspection.InspectionDetail.position";
    private int inspectionPosition;
    inspectionArrayTest inspection = new inspectionArrayTest();
    private ArrayList<violations> vList = inspection.getViolationList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);

        extractDataFromIntent();

        populateListView();
        registerClickCallBack();
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        inspectionPosition = intent.getIntExtra(EXTRA_POSITION, 0);
        //adjust arrayList by calling singleton method
    }

    public static Intent makeIntent(Context context, int position) {
        Intent intent = new Intent(context, InspectionDetail.class);
        intent.putExtra(EXTRA_POSITION, position);
        return intent;
    }

    private void registerClickCallBack() {
        ListView list = findViewById(R.id.inspectionListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                violations clickedViolation = vList.get(position);
                Toast.makeText(InspectionDetail.this, clickedViolation.getDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<violations> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.inspectionListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<violations> {
        public MyListAdapter(){
            super(InspectionDetail.this, R.layout.item_view, vList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            //Find current violation
            violations currentViolation = vList.get(position);

            //Fill the violation icon
            ImageView violationView = itemView.findViewById(R.id.item_violationIcon);
            violationView.setImageResource(currentViolation.getIconID());

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
//            return super.getView(position, convertView, parent);

        }

    }




    public class inspectionArrayTest{
        private ArrayList<violations> violationList = new ArrayList<>();

        public ArrayList<violations> getViolationList() {
            return violationList;
        }

        public inspectionArrayTest() {
            this.violationList.add(new violations(101, "non critical", "Plans/construction/alterations not in accordance with the Regulation [s. 3; s. 4],Not Repeat", "Pest", R.drawable.pest));
            this.violationList.add(new violations(101, "non critical", "Plans/construction/alterations not in accordance with the Regulation [s. 3; s. 4],Not Repeat", "Pest", R.drawable.pest));
            this.violationList.add(new violations(102, "non critical", "Operation of an unapproved food premises [s. 6(1)],Not Repeat", "Equipment", R.drawable.hygiene));
        }
    }

    public class violations{

        //100: permit
        //200: food
        //400: hygiene
        //500: foodsafe

        private int code;
        private String status;
        private String description;
        private String type;
        private int iconID;

        public violations(int code, String status, String description, String type, int iconID) {
            this.code = code;
            this.status = status;
            this.description = description;
            this.type = type;
            this.iconID = iconID;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        public String getDescription() {
            return description;
        }

        public String getType(){
            return type;
        }

        public int getIconID() {
            return iconID;
        }
    }

}
