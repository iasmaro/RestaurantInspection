package com.carbon.restaurantinspection.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;
import com.carbon.restaurantinspection.model.UpdateDownloader;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private UpdateDownloader updateDownloader;
    private Button downloadButton;
    private Button cancelButton;
    private Dialog myDialog;
    private Boolean mLocationPermissionsGranted = false;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean locationPermissionsGranted = false;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Hashtable<LatLng, Integer> markerIcons;
    private Hashtable <Integer, Integer> restaurantIndexHolder;
    private int restaurantIndex;
    static private ClusterManager<MyMarkerClass> CLUSTER_MANAGER;
    private List<MyMarkerClass> myMarkerClassList = new ArrayList<>();
    private RestaurantManager restaurantManager;
    private List<Restaurant> restaurantList;


    public static final String INTENT_NAME = "com/carbon/restaurantinspection/model/MainActivity.java:30";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // used rotate tutorial from
        // https://www.tutlane.com/tutorial/android/android-rotate-animations-clockwise-anti-clockwise-with-examples
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        markerIcons = new Hashtable<>();
        restaurantIndexHolder = new Hashtable<>();
        myDialog = new Dialog(this);
        getLocationPermission();
        toolbarBackButton();
    }

    private void setUpDownloadButton() {
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDownloader.downloadUpdates(MapActivity.this);
                final TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                final Animation rotate = AnimationUtils.loadAnimation(getApplicationContext()
                        , R.anim.rotate);
                TextView message = myDialog.findViewById(R.id.loadingMessage);
                message.setText(R.string.downloading);
                LinearLayout holder = myDialog.findViewById(R.id.buttonHolder);
                holder.removeView(downloadButton);
                cancelButton.setGravity(Gravity.CENTER);
                cancelDownload();
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        loadingIndicator.startAnimation(rotate);
                        if (!updateDownloader.downloadComplete() && !updateDownloader.downloadFailed()) {
                            handler.postDelayed(this, 1000);
                        } else if (updateDownloader.downloadComplete()){
                            finishDownload();
                        } else {
                            downloadFailed();
                        }
                    }
                };
                handler.post(runnable);
            }
        });
    }

    private void downloadFailed() {
        cancelButton.setText(R.string.finishButton);
        cancelButton.setBackgroundColor(getResources().getColor(R.color.finishBlue, null));
        TextView message = myDialog.findViewById(R.id.loadingMessage);
        message.setText(R.string.downloadFailed);
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.clearAnimation();
        LinearLayout downloadLayout = myDialog.findViewById(R.id.downloadLayout);
        downloadLayout.removeView(loadingIndicator);
        setUpCancelButton();
    }

    private void cancelDownload() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLoadingScreen();
                updateDownloader.cancelUpdate();
            }
        });
    }

    private void finishDownload() {
        cancelButton.setText(R.string.finishButton);
        cancelButton.setBackgroundColor(getResources().getColor(R.color.finishBlue, null));
        TextView message = myDialog.findViewById(R.id.loadingMessage);
        message.setText(R.string.finishDownload);
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.clearAnimation();
        LinearLayout downloadLayout = myDialog.findViewById(R.id.downloadLayout);
        downloadLayout.removeView(loadingIndicator);
        setUpCancelButton();
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
                    Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                    TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
                    loadingIndicator.startAnimation(rotate);
                    checkForUpdates();
                }
            }, 1000);
        } else {
            if (updateDownloader.updatesAvailable(MapActivity.this)){
                updatesAvailable();
            } else{
                stopLoadingScreen();
            }
        }
    }

    public void updatesAvailable() {
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.clearAnimation();
        loadingIndicator.setVisibility(View.INVISIBLE);
        TextView message = myDialog.findViewById(R.id.loadingMessage);
        message.setText(R.string.updatesAvailable);
        cancelButton.setVisibility(View.VISIBLE);
        downloadButton.setVisibility(View.VISIBLE);
        setUpCancelButton();
        setUpDownloadButton();
    }

    public void startLoadingScreen() {
        // used fragment tutorial from
        // https://www.youtube.com/watch?v=0DH2tZjJtm0
        myDialog.setContentView(R.layout.downloadscreen);
        downloadButton = myDialog.findViewById(R.id.downloadButton);
        cancelButton = myDialog.findViewById(R.id.cancelButton);
        cancelButton.setVisibility(View.INVISIBLE);
        downloadButton.setVisibility(View.INVISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        TextView loadingIndicator = myDialog.findViewById(R.id.loading_indicator);
        loadingIndicator.startAnimation(rotate);
        myDialog.show();
    }

    public void stopLoadingScreen() {
        myDialog.dismiss();
        initializeMap();
    }

    private void toolbarBackButton() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, RestaurantListActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                startActivity(new Intent(this, RestaurantListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        startLoadingScreen();
        updateDownloader = new UpdateDownloader(this);
        checkForUpdates();
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
            } else {
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
        locationPermissionsGranted = false;

        if (requestCode == 1234){
            if(grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        locationPermissionsGranted = false;
                        return;
                    }
                }
                locationPermissionsGranted = true;
                finish();
                startActivity(getIntent());
            }
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 41){
            if(resultCode == 42){
                String data1 = data.getStringExtra(INTENT_NAME);
                String data2 = data.getStringExtra(TAG);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (locationPermissionsGranted == true) {
            CLUSTER_MANAGER = new ClusterManager<>(this, this.googleMap);
            final MarkerClusterRenderer renderer = new MarkerClusterRenderer(this,
                    this.googleMap, CLUSTER_MANAGER);
            CLUSTER_MANAGER.setRenderer(renderer);
            this.googleMap.setOnCameraIdleListener(CLUSTER_MANAGER);
            this.googleMap.setOnMarkerClickListener(CLUSTER_MANAGER);
            this.googleMap.setOnInfoWindowClickListener(CLUSTER_MANAGER);
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            if(RestaurantDetailsActivity.lata == 0 && RestaurantDetailsActivity.longa == 0){
                getCurrentLocation();
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            clickCluster();
            clickClusterItem();
            setRestaurantMarkers();
            CLUSTER_MANAGER.addItems(myMarkerClassList);
            CLUSTER_MANAGER.cluster();
            CLUSTER_MANAGER.getMarkerCollection().setInfoWindowAdapter(
                    new ExtraInfoWindowAdapter(this));
        }

        if(RestaurantDetailsActivity.lata != 0 && RestaurantDetailsActivity.longa != 0){
            LatLng latLng11 = new LatLng(RestaurantDetailsActivity.lata, RestaurantDetailsActivity.longa);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng11, DEFAULT_ZOOM));
            ExtraInfoWindowAdapter viewWin = new ExtraInfoWindowAdapter(MapActivity.this);
        }
    }

    private void clickClusterItem() {
        CLUSTER_MANAGER.setOnClusterItemInfoWindowClickListener(new ClusterManager.
                OnClusterItemInfoWindowClickListener<MyMarkerClass>() {
            @Override
            public void onClusterItemInfoWindowClick(MyMarkerClass item) {

                int index = restaurantIndexHolder.get(item.getRestaurant_index());

                Intent intent = RestaurantDetailsActivity.makeIntent(MapActivity.this,
                        index);

                startActivity(intent);
            }
        });

    }

    private void clickCluster() {
        CLUSTER_MANAGER.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyMarkerClass>() {
            @Override
            public boolean onClusterClick(Cluster<MyMarkerClass> cluster) {
                if (cluster == null) return false;

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (MyMarkerClass user : cluster.getItems())

                    builder.include(user.getPosition());

                LatLngBounds bounds = builder.build();

                try {

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                } catch (Exception e) {

                    e.printStackTrace();

                }

                return true;
            }
        });

    }

    private void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(locationPermissionsGranted) {

            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        LatLng myLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        moveCamera(myLatLng, DEFAULT_ZOOM);

                    } else {
                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    // gets the Restaurant and Inspection Lists and helps set markers where appropriate
    private void setRestaurantMarkers() {
        restaurantManager = RestaurantManager.getInstance(this);

        restaurantList = restaurantManager.getRestaurantList();

        if(restaurantList != null){

            int num_of_restaurants = restaurantList.size();

            for(int i = 0; i < num_of_restaurants; i++) {
                Restaurant restaurant = restaurantList.get(i);
                String trackingNum = restaurant.getTrackingNumber();

                List<InspectionDetail> inspectionDetailList = InspectionManager.getInstance(this)
                        .getInspections(trackingNum);

                float latitude = (float) restaurant.getLatitude();
                float longitude = (float) restaurant.getLongitude();
                String name = restaurant.getName();

                if(inspectionDetailList != null) {

                    int size = inspectionDetailList.size();
                    InspectionDetail inspectionDetail = inspectionDetailList.get(size - 1);


                    placeMarker(new LatLng(latitude, longitude), DEFAULT_ZOOM, inspectionDetail,
                            restaurant, i);
                }
                else {
                    String address = restaurant.getPhysicalAddress();
                    placeMarker(new LatLng(latitude, longitude), DEFAULT_ZOOM, name, address, i);
                }
            }
        }
    }

    /**moves the camera to the location of the chosen restaurant given the restaurant HAS NO
     inspections**/
    private void placeMarker(LatLng latLng, float zoom, String title, String address, int index){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("Current location")) {

            final LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);

            myMarkerClassList.add(new MyMarkerClass(latLng1, title, address,
                    R.drawable.ic_warning_yellow_24dp, index));

            markerIcons.put(latLng, 0);

            restaurantIndexHolder.put(index, restaurantIndex);

            restaurantIndex++;
        }
    }

    /** moves the camera to the location of the chosen restaurant given the restaurant HAS an
     inspection **/
    private void placeMarker(LatLng latLng, float zoom, InspectionDetail inspectionDetail,
                             Restaurant restaurant, int index){

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(inspectionDetail != null){
            String snippet = "Address: " + restaurant.getPhysicalAddress() + "\n\n" +
                    "Hazard level " + inspectionDetail.getHazardLevel();

            String hazardLevel = inspectionDetail.getHazardLevel();
            int image_id;

            if (hazardLevel.equals("High")) {
                image_id = R.drawable.red_skull_crossbones;
            }
            else if (hazardLevel.equals("Moderate")) {
                image_id = R.drawable.ic_warning_yellow_24dp;
            }
            else {
                image_id = R.drawable.greencheckmark;
            }


            final LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);


            myMarkerClassList.add(new MyMarkerClass(latLng1, restaurant.getName(), snippet,
                    image_id, index));

            markerIcons.put(latLng, image_id);

            restaurantIndexHolder.put(index, restaurantIndex);

            restaurantIndex++;

        }
    }

    /**
     *  Class that creates the pop up display when a marker is clicked
     */
    class ExtraInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View view;

        ExtraInfoWindowAdapter(Context context) {
            view = LayoutInflater.from(context).inflate(R.layout.extra_info_window, null);
        }

        private void rendowWindowText(final Marker marker, View view1) {
            String title = marker.getTitle();
            TextView textView = view1.findViewById(R.id.extra_title);

            if (!title.equals("")) {
                textView.setText(title);
            }

            String snippet = marker.getSnippet();
            TextView textViewSnippet = view1.findViewById(R.id.snippet);

            if (snippet != null && !snippet.equals("")) {
                textViewSnippet.setText(snippet);
            }

            ImageView imageView = view1.findViewById(R.id.hazard_level);


            if (marker.getId() != null && markerIcons != null && markerIcons.size() > 0) {
                int image_id = markerIcons.get(marker.getPosition());
                if (image_id != 0) {
                    imageView.setImageResource(image_id);
                } else {
                    imageView.setImageResource(R.drawable.ic_warning_yellow_24dp);
                }
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            rendowWindowText(marker, view);
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            rendowWindowText(marker, view);
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Renderer is required for ClusterManager. Particularly to change the marker icon.
     */

    public static class MarkerClusterRenderer extends DefaultClusterRenderer<
            MyMarkerClass> {

        private static final int MARKER_DIMENSION = 90;

        private final IconGenerator iconGenerator;
        private final ImageView markerImageView;

        MarkerClusterRenderer(Context context, GoogleMap map,
                              ClusterManager<MyMarkerClass> clusterManager) {
            super(context, map, clusterManager);

            iconGenerator = new IconGenerator(context);

            markerImageView = new ImageView(context);

            markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION,
                    MARKER_DIMENSION));

            iconGenerator.setContentView(markerImageView);

        }

        @Override
        protected void onBeforeClusterItemRendered(MapActivity.MyMarkerClass item,
                                                   MarkerOptions markerOptions) {

            markerImageView.setImageResource(item.getVectorID());

            Bitmap icon = iconGenerator.makeIcon();

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

            markerOptions.title(item.getTitle());

            markerOptions.snippet(item.getSnippet());

        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }

    /**
     *  Class that imitates a marker. Stores all the information a marker does.
     *  Required for using ClusterManager
     */
    public static class MyMarkerClass implements ClusterItem {

        private final LatLng position;
        private final String title;
        private final String snippet;
        private final int vectorID;
        private final int restaurant_index;

        MyMarkerClass(LatLng position, String title, String snippet, int vectorID,
                      int restaurant_index) {
            this.position = position;
            this.title = title;
            this.snippet = snippet;
            this.vectorID = vectorID;
            this.restaurant_index = restaurant_index;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }

        int getVectorID(){
            return vectorID;
        }

        int getRestaurant_index(){
            return restaurant_index;
        }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//    }

}
