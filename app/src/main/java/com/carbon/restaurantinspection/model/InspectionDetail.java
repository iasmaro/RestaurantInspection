package com.carbon.restaurantinspection.model;

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

    public String[] getViolations() {
        return violations;
    }

    public void setViolations(String[] violations) {
        this.violations = violations;
    }
}