package com.carbon.restaurantinspection.model;

import java.util.ArrayList;

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
    private ArrayList<Violation> violations= new ArrayList<>();

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
        for (String violation: strViolations) {
            if (violation.length() > 10) {
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

    public String getInspectionDate() {
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
}
