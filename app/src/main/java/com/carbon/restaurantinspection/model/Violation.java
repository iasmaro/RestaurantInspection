package com.carbon.restaurantinspection.model;

import android.util.Log;

import java.util.Hashtable;

/*
  Violation class models a violation found in an inspection report.
  It contains the violation's code, status, description and type.
 */
public class Violation {
    private String code;
    private String status;
    private String description;
    private String type;

    public Violation(String violation) {
        String[] violationArray = violation.split(",");
        if (violationArray.length > 0) {
            code = violationArray[0];
            status = violationArray[1];
            if (violationArray.length > 3) {
                description = violationArray[2] + "," + violationArray[3];
            } else {
                description = violationArray[2];
            }
            setTypeFromCode();
        }
    }

    private void setTypeFromCode() {
        Hashtable<String, String> threeCode = new Hashtable<>();
        threeCode.put("311", "Permit");
        threeCode.put("312", "Permit");
        threeCode.put("306", "Food");
        threeCode.put("304", "Pest");
        threeCode.put("305", "Pest");
        threeCode.put("314", "Hygiene");
        threeCode.put("311", "Permit");
        threeCode.put("311", "Permit");
        Hashtable<String, String> otherCode = new Hashtable<>();
        otherCode.put("1", "Permit");
        otherCode.put("2", "Food");
        otherCode.put("4", "Hygiene");
        otherCode.put("5", "Foodsafe");
        if (code.substring(0, 1).equals("3")) {
            if (threeCode.containsKey(code)) {
                this.type = threeCode.get(code);
            } else {
                this.type = "Equipment";
            }
        } else {
            this.type = otherCode.get(code.substring(0, 1));
        }
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
