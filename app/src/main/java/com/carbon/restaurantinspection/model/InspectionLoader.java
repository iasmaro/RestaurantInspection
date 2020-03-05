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

    private void addInspectionDetail(String[] tokens) {
        int criticalIssues = Integer.parseInt(tokens[3]);
        int nonCriticalIssues = Integer.parseInt(tokens[4]);
        String[] violationsArray;
        if (tokens.length >= 6) {
            violationsArray = tokens[6].split("|");
        } else {
            violationsArray = null;
        }
        InspectionDetail inspection = new InspectionDetail(tokens[0], tokens[1], tokens[2],
                criticalIssues, nonCriticalIssues, tokens[5], violationsArray);
        if (inspections.containsKey(tokens[0])) {
            inspections.get(tokens[0]).add(inspection);
        }
        else {
            ArrayList<InspectionDetail> inspectionList = new ArrayList<>();
            inspectionList.add(inspection);
            inspections.put(tokens[0], inspectionList);
        }
    }
}
