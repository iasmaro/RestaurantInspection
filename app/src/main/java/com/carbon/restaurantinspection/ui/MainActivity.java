package com.carbon.restaurantinspection.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.carbon.restaurantinspection.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
/** Set up the map view and ensure services from google maps API**/
public class MainActivity extends AppCompatActivity {
    // reference code from Youtuber: CodingWithMitch Playlist: Google Maps & Google Places Android Course
    //https://www.youtube.com/watch?v=urLA8z6-l3k&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=2
    //https://www.youtube.com/watch?v=M0bYvXlhgSI&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=3
    private static final int ERROR_DIALOG_REQUEST = 9001;

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()) {
            finish();
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        }
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
