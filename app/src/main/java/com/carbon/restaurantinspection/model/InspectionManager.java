package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;

/*
  InspectionManager class is a singleton class containing inspection details of restaurants.
 */
public class InspectionManager {
    private static Hashtable<String, ArrayList<InspectionDetail>> inspections;
    private static InspectionManager instance;

    public static InspectionManager getInstance(Context context) {
        if (instance == null) {
            instance = new InspectionManager(context);
        }
        return instance;
    }

    private InspectionManager(Context context) {
        InspectionLoader loader = new InspectionLoader();
        inspections = loader.loadInspectionDetailList(context);
    }

    public ArrayList<InspectionDetail> getInspections(String trackingNumber) {
        return inspections.get(trackingNumber);
    }
}

