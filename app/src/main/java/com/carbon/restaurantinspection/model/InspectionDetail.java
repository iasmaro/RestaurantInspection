package com.carbon.restaurantinspection.model;


import android.content.Context;
import android.util.Log;

import com.carbon.restaurantinspection.R;

import java.util.ArrayList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;


/**
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
    private ArrayList<Violation> violations= new ArrayList<>();
    private final int MILISEC_TO_DAYS = 1000*3600*24;
    Context context;

    public InspectionDetail(String trackingNumber, String inspectionDate, String inspectionType,
                            int numCritical, int numNonCritical, String hazardLevel, String[] violations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardLevel = hazardLevel;
        if (violations != null) {
            addViolations(violations);
        }
    }

    private void addViolations(String[] strViolations) {
        for (String violation: strViolations) {
            if (violation.length() > 10) {
                Violation viol = new Violation(violation);
                violations.add(viol);
            }
        }
    }

    public String getInspectionDate(Context context) {
        Calendar inspecDate = getInspecDate();
        Calendar monthAgo = getMonthAgo();
        Calendar yearAgo = getYearAgo();

        String dateOfInspection = "";
        if (monthAgo.before(inspecDate)) {
            int days = daysBetween(inspecDate);
            String daysAgo = context.getResources().getString(R.string.daysAgo);
            dateOfInspection = days + " " + daysAgo;
        }
        else if (yearAgo.before(inspecDate)) {
            String month = getMonth(inspectionDate.substring(4, 6), context);
            dateOfInspection = month + " " + inspecDate.get(inspecDate.DAY_OF_MONTH);
        }
        else {
            String month = getMonth(inspectionDate.substring(4, 6),context);
            int year = inspecDate.get(Calendar.YEAR);
            dateOfInspection = month + " " + year;
        }
        return dateOfInspection;
    }

    public boolean isLessThanYearAgo() {
        Calendar inspectionDate = getInspecDate();
        Calendar yearAgo = getYearAgo();
        return yearAgo.before(inspectionDate);
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

    public String getFullDate(Context context) {
        String fullDate = getMonth(inspectionDate.substring(4, 6), context) + " "
                + inspectionDate.substring(6) + ", "
                + inspectionDate.substring(0, 4);
        return fullDate;
    }

    private String getMonth(String month, Context context) {
        Hashtable<String, String> months = new Hashtable<>();
        String january = context.getResources().getString(R.string.january);
        months.put("01", january);
        String february = context.getResources().getString(R.string.february);
        months.put("02", february);
        String march = context.getResources().getString(R.string.march);
        months.put("03", march);
        String april = context.getResources().getString(R.string.april);
        months.put("04", april);
        String may = context.getResources().getString(R.string.may);
        months.put("05", may);
        String june = context.getResources().getString(R.string.june);
        months.put("06", june);
        String july = context.getResources().getString(R.string.july);
        months.put("07", july);
        String august = context.getResources().getString(R.string.august);
        months.put("08", august);
        String september = context.getResources().getString(R.string.september);
        months.put("09", september);
        String october = context.getResources().getString(R.string.october);
        months.put("10", october);
        String november = context.getResources().getString(R.string.november);
        months.put("11", november);
        String december = context.getResources().getString(R.string.december);
        months.put("12", december);
        return months.get(month);
    }



    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStrInspectionDate() {
        return inspectionDate;
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
        String str = getInspectionDate(context)+":\n"+numCritical+" critical issues\n"+numNonCritical+" non critical issues";
        return str;
    }
}
