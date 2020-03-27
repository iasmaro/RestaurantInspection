package com.carbon.restaurantinspection.model;

import android.content.Context;
import android.util.Log;

import com.carbon.restaurantinspection.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;

/**
  InspectionManager class is a singleton class containing inspection details of restaurants.
 */
public class InspectionManager {
    private Hashtable<String, ArrayList<InspectionDetail>> inspections = new Hashtable<>();
    private static InspectionManager instance;

    public static InspectionManager getInstance(Context context) {
        if (instance == null) {
            instance = new InspectionManager(context);
        }
        return instance;
    }

    private InspectionManager(Context context) {
        File csvFile = context.getFileStreamPath("inspections.csv");
        CSVLoader loader = new CSVLoader();
        ArrayList<String> file;
        if (csvFile.isFile()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(csvFile);
                file = loader.readCSV(fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                InputStream is = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
                file = loader.readCSV(is);
            }
        } else {
            InputStream is = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
            file = loader.readCSV(is);
        }
        parseFile(file);
        sortInspectionDetails();
    }

    private void sortInspectionDetails() {
        Set<String> trackingNumbers = inspections.keySet();
        for (String trackingNum : trackingNumbers) {
            ArrayList<InspectionDetail> inspectionList = inspections.get(trackingNum);
            Collections.sort(inspectionList, new Comparator<InspectionDetail>() {
                @Override
                public int compare(InspectionDetail inspectionDetail, InspectionDetail otherinspection) {
                    return otherinspection.getStrInspectionDate().compareTo(inspectionDetail.getStrInspectionDate());
                }
            });
        }
    }

    private void parseFile(ArrayList<String> file) {
        for (String line : file) {
            String[] tokens = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            addInspectionDetail(tokens);
        }
    }

    private void addInspectionDetail(String[] tokens) {
        int criticalIssues = Integer.parseInt(tokens[3]);
        int nonCriticalIssues = Integer.parseInt(tokens[4]);
        String[] violationsArray;
        if (tokens.length == 7) {
            String violations = tokens[6].split("\"")[1];
            violationsArray = violations.split("\\|");
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

    public ArrayList<InspectionDetail> getInspections(String trackingNumber) {
        return inspections.get(trackingNumber);
    }
}
