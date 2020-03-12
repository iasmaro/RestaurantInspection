package com.carbon.restaurantinspection.ui;

import androidx.appcompat.app.AppCompatActivity;
import com.carbon.restaurantinspection.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RestaurantDetailsActivity extends AppCompatActivity {
    public static final String INTENT_NAME = "com/carbon/restaurantinspection/model/MainActivity.java:30";
    private int index;

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        return intent;
    }

    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(INTENT_NAME, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        getIntents();
    }

    /**private void extractDataFromIntent() {
        Intent intent = getIntent(RestaurantDetailsActivity.this, position);
    }**/
    private void getIntents() {
        Intent intent = getIntent();
        index = intent.getIntExtra(INTENT_NAME, 0);
    }

}
