package com.carbon.restaurantinspection.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Read CSV files and add each line into an ArrayList.
 */
public class CSVLoader {
    private ArrayList<String> csv = new ArrayList<>();

    public ArrayList<String> readCSV(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            // Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (!line.equals(",,,,,,")) {
                    csv.add(line);
                }
            }
        } catch (IOException e) {
            Log.wtf("CSVLoader", "Error reading data file on line " + line, e);
        }
        return csv;
    }
}
