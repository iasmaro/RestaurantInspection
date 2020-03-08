package com.carbon.restaurantinspection.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;

/*
  InspectionDetail class models an inspection report's details.
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
    private String[] violations;
    private final int MILISEC_TO_DAYS = 1000*3600*24;

    public InspectionDetail(String trackingNumber, String inspectionDate, String inspectionType, 
    int numCritical, int numNonCritical, String hazardLevel, String[] violations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardLevel = hazardLevel;
        this.violations = violations;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getInspectionDate() {
        Date date = getDate();
        Calendar inspecDate = new GregorianCalendar();
        inspecDate.setTime(date);
        Calendar monthAgo = new GregorianCalendar();
        monthAgo.add(monthAgo.DAY_OF_MONTH, -30);
        Calendar yearAgo = new GregorianCalendar();
        yearAgo.add(yearAgo.YEAR, -1);
        String dateOfInspection = "";
        if (monthAgo.before(inspecDate)) {
            Calendar today = Calendar.getInstance();
            long milliSecs = today.getTimeInMillis() - inspecDate.getTimeInMillis();
            int days = (int) (milliSecs / MILISEC_TO_DAYS);
            dateOfInspection = days + " days ago.";
        }
        else if (yearAgo.before(inspecDate)) {
            String month = inspecDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CANADA);
            dateOfInspection = month + " " + inspecDate.DAY_OF_MONTH;
        }
        else {
            String month = inspecDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CANADA);
            int year = inspecDate.YEAR;
            dateOfInspection = month + " " + year;
        }
        return dateOfInspection;
    }

    public String getFullDate() {
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
        String fullDate = months.get(inspectionDate.substring(4, 6)) + " "
                + inspectionDate.substring(6) + ", "
                + inspectionDate.substring(0, 4);
        return fullDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    private Date getDate(){
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            return dateFormat.parse(inspectionDate);
        } catch (ParseException e) {
            return new Date();
        }
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

    public String[] getViolations() {
        return violations;
    }

    public void setViolations(String[] violations) {
        this.violations = violations;
    }
}
