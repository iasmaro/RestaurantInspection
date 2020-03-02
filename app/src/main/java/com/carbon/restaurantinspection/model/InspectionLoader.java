package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.util.Log;

import com.carbon.restaurantinspection.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;

public class InspectionLoader {
    private Hashtable<String, ArrayList<InspectionDetail>> inspections = new Hashtable<>();

    public Hashtable<String, ArrayList<InspectionDetail>> loadInspectionDetailList(Context context) {
        readInspectionDetails(context);
        return inspections;
    }

    private void readInspectionDetails(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            // Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                // Split by ','
                String[] tokens = line.split(",");

                // Read the data
                addInspectionDetail(tokens);
            }
        } catch (IOException e) {
            Log.wtf("InspectionLoader", "Error reading data file on line " + line, e);
        }
    }

    private void addInspectionDetail(String[] components) {
        InspectionDetail inspection = new InspectionDetail(components[0], components[1], components[2], components[3],
                                                            components[4], components[5], components[6]);
        if (inspections.containsKey(components[0])) {
            inspections.get(components[0]).add(inspection);
        }
        else {
            ArrayList<InspectionDetail> inspectionList = new ArrayList<>();
            inspectionList.add(inspection);
            inspections.put(components[0], inspectionList);
        }
    }
}
