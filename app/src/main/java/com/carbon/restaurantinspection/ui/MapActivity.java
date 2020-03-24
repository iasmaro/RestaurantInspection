package com.carbon.restaurantinspection.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.UpdateDownloader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private UpdateDownloader updateDownloader;
    private LinearLayout loadingScreen;
    private Button downloadButton;
    private Button cancelButton;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        loadingScreen = findViewById(R.id.loadingScreen);
        downloadButton = findViewById(R.id.downloadButton);
        cancelButton = findViewById(R.id.cancelButton);
        loadingScreen.setVisibility(View.INVISIBLE);
        downloadButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        startLoadingScreen();
        updateDownloader = new UpdateDownloader(this);
        checkForUpdates();
        setUpCancelButton();
        setUpDownloadButton();

        getLocationPermission();
    }

    private void setUpDownloadButton() {
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDownloader.downloadUpdates(MapActivity.this);
                final TextView loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                final Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                TextView message = findViewById(R.id.loadingMessage);
                message.setText(R.string.downloading);
                cancelButton.setVisibility(View.VISIBLE);
                downloadButton.setVisibility(View.INVISIBLE);
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        loadingIndicator.startAnimation(rotate);
                        if (!updateDownloader.downloadComplete()) {
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.post(runnable);
            }
        });
    }

    private void setUpCancelButton() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLoadingScreen();
            }
        });
    }

    public void checkForUpdates() {
        Handler handler = new Handler();
        if(!updateDownloader.isReady()) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    checkForUpdates();
                }
            }, 100);
        } else {
            if (updateDownloader.updatesAvailable(MapActivity.this)){
                updatesAvailable();
            } else{
                stopLoadingScreen();
            }
        }
    }

    public void updatesAvailable() {
        TextView loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.clearAnimation();
        loadingIndicator.setVisibility(View.INVISIBLE);
        TextView message = findViewById(R.id.loadingMessage);
        message.setText(R.string.updatesAvailable);
        cancelButton.setVisibility(View.VISIBLE);
        downloadButton.setVisibility(View.VISIBLE);
    }

    public void startLoadingScreen() {
        loadingScreen.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        TextView loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.startAnimation(rotate);
    }

    public void stopLoadingScreen() {
        loadingScreen.setVisibility(View.INVISIBLE);
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            } else {
                //ask for permissions
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initializeMap();
                }
            }
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
