package com.carbon.restaurantinspection.ui;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

    public class MyMarkerClass implements ClusterItem {

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

