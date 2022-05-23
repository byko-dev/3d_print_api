package com.byko.api_3d_printing.model;

public class ConfigurationResponse {

    public String email;
    public boolean enabled;

    public ConfigurationResponse(String email, boolean enabled) {
        this.email = email;
        this.enabled = enabled;
    }
    public ConfigurationResponse(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
