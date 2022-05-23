package com.byko.api_3d_printing.model;

public class ChangeStatusRequest {
    public ChangeStatusRequest() {
        projectId = "0";
        newStatus = 0;
    }
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    public Integer getNewStatus() {
        return newStatus;
    }
    public void setNewStatus(Integer newStatus) {
        this.newStatus = newStatus;
    }
    public String projectId;
    public Integer newStatus;
}

