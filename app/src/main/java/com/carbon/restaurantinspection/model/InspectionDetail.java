package com.carbon.restaurantinspection.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/*
  InspectionDetails class models an inspection report's details.
  It contains the inspection's tracking number, date, type, number of critical issues found,
  number of non-critical issues found, hazard level, and a summary of all the violations.
 */
public class InspectionDetail {
    private String trackingNumber;
    private String inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardLevel;
    private ArrayList<Violation> violations= new ArrayList<>();
    private final int MILISEC_TO_DAYS = 1000*3600*24;


    public InspectionDetail(String trackingNumber, String inspectionDate, String inspectionType, int numCritical, int numNonCritical, String hazardLevel, String[] violations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardLevel = hazardLevel;
        addViolations(violations);

    }

    private void addViolations(String[] strViolations) {
        if (strViolations == null) {
            return;
        }
        for (String violation : strViolations) {
            if (!violation.isEmpty()) {
                Log.d("lol", "0" + violation);
                Violation viol = new Violation(violation);
                violations.add(viol);
            }
        }
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public ArrayList<Violation> getViolations() {
        return violations;
    }

    public void setViolations(String[] violations) {
        addViolations(violations);
    }

    public String returnInsDetails() {
        String str = getInspectionDate()+":\n"+numCritical+" critical issues\n"+numNonCritical+" non critical issues";
        return str;
    }

    public String getInspectionDate() {
        Calendar inspecDate = getInspecDate();
        Calendar monthAgo = getMonthAgo();
        Calendar yearAgo = getYearAgo();

        String dateOfInspection = "";
        if (monthAgo.before(inspecDate)) {
            int days = daysBetween(inspecDate);
            dateOfInspection = days + " days ago.";
        }
        else if (yearAgo.before(inspecDate)) {
            String month = getMonth(inspectionDate.substring(4, 6));
            dateOfInspection = month + " " + inspecDate.DAY_OF_MONTH;
        }
        else {
            String month = getMonth(inspectionDate.substring(4, 6));
            int year = inspecDate.YEAR;
            dateOfInspection = month + " " + year;
        }
        return dateOfInspection;
    }

    private Calendar getInspecDate(){
        Calendar inspecDate = new GregorianCalendar();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = dateFormat.parse(inspectionDate);
            inspecDate.setTime(date);
            return inspecDate;
        } catch (ParseException e) {
            return inspecDate;
        }
    }

    private Calendar getYearAgo() {
        Calendar yearAgo = new GregorianCalendar();
        yearAgo.add(yearAgo.YEAR, -1);
        return yearAgo;
    }

    private Calendar getMonthAgo() {
        Calendar monthAgo = new GregorianCalendar();
        monthAgo.add(monthAgo.DAY_OF_MONTH, -30);
        return monthAgo;
    }

    private int daysBetween(Calendar inspecDate) {
        Calendar today = Calendar.getInstance();
        long milliSecs = today.getTimeInMillis() - inspecDate.getTimeInMillis();
        int days = (int) (milliSecs / MILISEC_TO_DAYS);
        return days;
    }

    public String getFullDate() {
        String fullDate = getMonth(inspectionDate.substring(4, 6)) + " "
                + inspectionDate.substring(6) + ", "
                + inspectionDate.substring(0, 4);
        return fullDate;
    }

    private String getMonth(String month) {
        Hashtable<String, String> months = new Hashtable<>();
        months.put("01", "January");
        months.put("02", "February");
        months.put("03", "March");
        months.put("04", "April");
        months.put("05", "May");
        months.put("06", "June");
        months.put("07", "July");
        months.put("08", "August");
        months.put("09", "September");
        months.put("10", "October");
        months.put("11", "November");
        months.put("12", "December");
        return months.get(month);
    }

}
