package com.carbon.restaurantinspection.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.InspectionDetails;
import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.userinterface.RestaurantDetails;

public class MainActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        setUpBtn();
    }

    private void setUpBtn() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InspectionDetails.class);
                startActivity(intent);
            }
        });
    }
}
