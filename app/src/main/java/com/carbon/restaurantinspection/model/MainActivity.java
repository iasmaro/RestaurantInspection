package com.carbon.restaurantinspection.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.R;

public class MainActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        setUpBtn();
//        int index = 1;
//        Intent intent = new Intent(this,RestaurantDetailActivity.class);
//        intent.putExtra("ca/sfu/restaurantinspections/MainActivity.java:14", index);
        //       startActivity(intent);
    }

    private void setUpBtn() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 3;
                Intent intent = new Intent(MainActivity.this, RestaurantDetails.class);
                intent.putExtra("com/carbon/restaurantinspection/model/MainActivity.java:30",index);
                startActivity(intent);
            }
        });
    }
}
