package com.carbon.restaurantinspection.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.carbon.restaurantinspection.R;

public class filterFragment extends Activity {
    Button ok;
    RadioButton low, medium, high;
    Boolean greater = true;
    String hazardLevel;
    int numOfCrit = -1;
    Boolean favouriteChecked = false;
    CheckBox favourites;
    TextView greaterOrEqual, lessOrEqual;
    EditText txtLess, txtGreater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter_fragment);

        popUpConfiguration();
        setUpRadioBtns();
        setUpFavourites();
        acceptFilter();
        setUpTextViews();
    }



    private void setUpTextViews() {
        greaterOrEqual = findViewById(R.id.greaterOrEqual);
        txtGreater = findViewById(R.id.editTextGreaterOrEqual);
        txtLess = findViewById(R.id.editTextForLessOrEqual);
        greaterOrEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    txtGreater.setVisibility(View.VISIBLE);
                    txtLess.setVisibility(View.INVISIBLE);
                    greater = true;
            }
        });

        lessOrEqual = findViewById(R.id.lessOrEqual);
        lessOrEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               txtLess.setVisibility(View.VISIBLE);
               txtGreater.setVisibility(View.INVISIBLE);
               greater = false;
            }
        });
    }

    private void acceptFilter() {
        ok = findViewById(R.id.acceptFilter);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(greater) {
                    String temp = txtGreater.getText().toString();
                    if(temp.equals("")){
                        numOfCrit = 0;
                    }
                    else {
                        numOfCrit = Integer.parseInt(temp);
                    }
                }
                else{
                    String temp = txtLess.getText().toString();
                    if(temp.equals("")){
                        numOfCrit = 0;
                    }
                    else {
                        numOfCrit = Integer.parseInt(temp);
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("com.carbon.restaurantinspection.ui.filterFragment.favouriteChecked",
                        favouriteChecked);
                intent.putExtra("com.carbon.restaurantinspection.ui.filterFragment.hazardLevel",
                        hazardLevel);
                intent.putExtra("com.carbon.restaurantinspection.ui.filterFragment.numOfCrit",
                        numOfCrit);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setUpFavourites() {
        favourites = findViewById(R.id.favourites);
        if(favourites.isChecked()){
            favouriteChecked = true;
        }
    }

    private void setUpRadioBtns() {
        low = findViewById(R.id.lowRadioBtn);
        medium = findViewById(R.id.medRadioBtn);
        high = findViewById(R.id.highRadioBtn);

        if(low.isChecked()){
            hazardLevel = "low";
        }
        else if(high.isChecked()){
            hazardLevel = "high";
        }
        else{
            hazardLevel = "medium";
        }
    }

    private void popUpConfiguration() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int)(height * .7));
    }
}
