package com.byko.api_3d_printing.model;

public class AuthenticationResponse {

    private String jwt;

    public AuthenticationResponse(){}

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
