package com.byko.api_3d_printing.model;

public class StatusModel {

    private String status;

    public StatusModel(String status) {
        this.status = status;
    }

    public StatusModel(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
