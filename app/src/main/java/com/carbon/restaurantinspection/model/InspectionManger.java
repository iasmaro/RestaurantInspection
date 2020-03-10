package com.carbon.restaurantinspection.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Hashtable;

/*
  InspectionManager class is a singleton class containing inspection details of restaurants.
 */
public class InspectionManger {
    private Hashtable<String, ArrayList<InspectionDetail>> inspections;
    private static InspectionManger instance;

    public static InspectionManger getInstance(Context context) {
        if (instance == null) {
            instance = new InspectionManger(context);
        }
        return instance;
    }

    private InspectionManger(Context context) {
        InspectionLoader loader = new InspectionLoader();
        inspections = loader.loadInspectionDetailList(context);
    }

    public ArrayList<InspectionDetail> getInspections(String trackingNumber) {
        return inspections.get(trackingNumber);
    }
}
