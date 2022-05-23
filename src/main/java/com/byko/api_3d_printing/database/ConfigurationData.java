package com.byko.api_3d_printing.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "configuration")
public class ConfigurationData {

    @Id
    private String id;
    private String email;
    private String emailPass;
    private boolean emailEnable;

    public ConfigurationData(String email, String emailPass, boolean emailEnable) {
        this.email = email;
        this.emailPass = emailPass;
        this.emailEnable = emailEnable;
    }

    public ConfigurationData(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPass() {
        return emailPass;
    }

    public void setEmailPass(String emailPass) {
        this.emailPass = emailPass;
    }

    public boolean isEmailEnable() {
        return emailEnable;
    }

    public void setEmailEnable(boolean emailEnable) {
        this.emailEnable = emailEnable;
    }
}
