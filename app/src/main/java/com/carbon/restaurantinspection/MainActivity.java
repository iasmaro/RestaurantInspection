package com.carbon.restaurantinspection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list_activity);
        Intent intent = new Intent(this, InspectionDetail.class);
        startActivity(intent);
    }
}
