package com.carbon.restaurantinspection.model;

public class Violation {
    private String code;
    private String status;
    private String description;
    private String type;
    private String repeat;

    public Violation(String code, String status, String description, String type, String repeat) {
        this.code = code;
        this.status = status;
        this.description = description;
        this.type = type;
        this.repeat = repeat;
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

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
