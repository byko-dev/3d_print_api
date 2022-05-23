package com.byko.api_3d_printing.model;

public class ConfigurationUpdate {

    public String email;
    public String password;
    public boolean enabled;

    public ConfigurationUpdate(String email, String password, boolean enabled) {
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    public ConfigurationUpdate(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
