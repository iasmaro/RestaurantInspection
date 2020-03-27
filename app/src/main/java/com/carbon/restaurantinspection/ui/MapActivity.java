package com.carbon.restaurantinspection.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.carbon.restaurantinspection.R;
import com.carbon.restaurantinspection.model.InspectionDetail;
import com.carbon.restaurantinspection.model.InspectionManager;
import com.carbon.restaurantinspection.model.Restaurant;
import com.carbon.restaurantinspection.model.RestaurantManager;
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

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final float DEFAULT_ZOOM = 15f;


    //vars

    private Boolean mLocationPermissionsGranted = false;

    private GoogleMap googleMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Hashtable<LatLng, Integer> marker_icons;

    // Hashtable that takes an integer as the key and returns the restaurant_index
    private Hashtable <Integer, Integer> restaurant_index_holder;

    private int restaurant_index; // Stores the index associated with each restaurant

    static private ClusterManager<MyMarkerClass> clusterManager;

    //array list of MyMarkerClass items (These represent markers.)
    private List<MyMarkerClass> myMarkerClassList = new ArrayList<>();

    private RestaurantManager restaurantManager;

    private List<Restaurant> restaurantList;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        marker_icons = new Hashtable<>();

        restaurant_index_holder = new Hashtable<>();

        getLocationPermission();

    }



    // gets the Restaurant and Inspection Lists and helps set markers where appropriate

    private void setRestaurantMarkers(){

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


                else{
                    String address = restaurant.getPhysicalAddress();
                    placeMarker(new LatLng(latitude, longitude), DEFAULT_ZOOM, name, address, i);
                }


            }

        }


    }



    private void getDeviceLocation(){

        // I made my location Surrey central so I can see the Restaurants easily

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        try{

            if(mLocationPermissionsGranted){



                final Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {

                    @Override

                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful()){

                            Location currentLocation = (Location) task.getResult();

                            currentLocation.setLatitude(49.1867);

                            currentLocation.setLongitude(-122.8494);

                            placeMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),

                                    DEFAULT_ZOOM, "Current location", "", 0);



                        }

                    }

                });

            }

        }catch (SecurityException ignored){

        }

    }



    // places marker at the location of the chosen restaurant given the restaurant has no
    // inspections

    private void placeMarker(LatLng latLng, float zoom, String title, String address, int index){

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("Current location")) {

            final LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);

            myMarkerClassList.add(new MyMarkerClass(latLng1, title, address,
                    R.drawable.ic_warning_yellow_24dp, index));

            marker_icons.put(latLng, 0);

            restaurant_index_holder.put(index, restaurant_index);

            restaurant_index++;
        }
    }



    // places marker at the location of the chosen restaurant given the restaurant has an
    // inspection

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

            marker_icons.put(latLng, image_id);

            restaurant_index_holder.put(index, restaurant_index);

            restaurant_index++;

        }


    }



    private void initializeMap(){


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);


    }



    private void getLocationPermission(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,

                Manifest.permission.ACCESS_COARSE_LOCATION};



        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),

                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),

                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                mLocationPermissionsGranted = true;

                initializeMap();

            }

            else{
                //ask for permissions
                ActivityCompat.requestPermissions(this,

                        permissions,

                        LOCATION_PERMISSION_REQUEST_CODE);

            }

        }

        else{
            ActivityCompat.requestPermissions(this,

                    permissions,

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



    @Override

    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;

        if (mLocationPermissionsGranted) {

            clusterManager = new ClusterManager<>(this, this.googleMap);

            final MarkerClusterRenderer renderer = new MarkerClusterRenderer(this,
                    this.googleMap, clusterManager);


            clusterManager.setRenderer(renderer);

            this.googleMap.setOnCameraIdleListener(clusterManager);

            this.googleMap.setOnMarkerClickListener(clusterManager);

            this.googleMap.setOnInfoWindowClickListener(clusterManager);

            this.googleMap.setMyLocationEnabled(true);

            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);


            clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyMarkerClass>() {
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


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,

                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;

            }

            //On info window click go to RestaurantDetails

            clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.
                    OnClusterItemInfoWindowClickListener<MyMarkerClass>() {
                @Override
                public void onClusterItemInfoWindowClick(MyMarkerClass item) {

                    int index = restaurant_index_holder.get(item.getRestaurant_index());

                    Intent intent = RestaurantDetailsActivity.makeIntent(MapActivity.this,
                            index);

                    startActivity(intent);
                }
            });

            getDeviceLocation();

            setRestaurantMarkers();

            clusterManager.addItems(myMarkerClassList);

            clusterManager.cluster();

            // assigns custom view to clusterManager
            clusterManager.getMarkerCollection().setInfoWindowAdapter(
                    new ExtraInfoWindowAdapter(this));

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


            if (marker.getId() != null && marker_icons != null && marker_icons.size() > 0) {
                int image_id = marker_icons.get(marker.getPosition());
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


    /**
     * Renderer is required for ClusterManager. Particularly to change the marker icon.
     */
    public static class MarkerClusterRenderer extends DefaultClusterRenderer<MapActivity.
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

        public MyMarkerClass(LatLng position, String title, String snippet, int vectorID,
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

        public int getVectorID(){
            return vectorID;
        }

        public int getRestaurant_index(){
            return restaurant_index;
        }

    }





}
