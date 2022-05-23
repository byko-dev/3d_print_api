package com.byko.api_3d_printing.model;

public class LoginRequest {

    public String username;
    public String password;
    public String captchaResponse;

    public LoginRequest(String username, String password, String captchaResponse) {
        this.username = username;
        this.password = password;
        this.captchaResponse = captchaResponse;
    }

    public LoginRequest(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }
}
