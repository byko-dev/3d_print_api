package com.byko.api_3d_printing.model;

public class ChangePasswordRequest {

    public String password;
    public String newPassword;

    public ChangePasswordRequest() {}

    public ChangePasswordRequest(String password, String newPassword) {
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
