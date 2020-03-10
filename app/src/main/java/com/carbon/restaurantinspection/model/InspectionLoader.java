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
                String[] tokens = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                // Read the data
                addInspectionDetail(tokens);
            }
        } catch (IOException e) {
            Log.wtf("InspectionLoader", "Error reading data file on line " + line, e);
        }
    }

    private void addInspectionDetail(String[] tokens) {
        int criticalIssues = Integer.parseInt(tokens[3]);
        int nonCriticalIssues = Integer.parseInt(tokens[4]);
        String[] violationsArray;
        if (tokens.length == 7) {
            String[] violations = tokens[6].split("\"");
            violationsArray = violations[1].split("\\|");
        } else {
            violationsArray = null;
        }
        String trackingNumber = tokens[0].split("\"")[1];
        String type = tokens[2].split("\"")[1];
        String hazard = tokens[5].split("\"")[1];
        InspectionDetail inspection = new InspectionDetail(trackingNumber, tokens[1], type,
                criticalIssues, nonCriticalIssues, hazard, violationsArray);
        if (inspections.containsKey(trackingNumber)) {
            inspections.get(trackingNumber).add(inspection);
        }
        else {
            ArrayList<InspectionDetail> inspectionList = new ArrayList<>();
            inspectionList.add(inspection);
            inspections.put(trackingNumber, inspectionList);
        }

    }
}


