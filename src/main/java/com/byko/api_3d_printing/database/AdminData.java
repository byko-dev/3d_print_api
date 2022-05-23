package com.byko.api_3d_printing.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "adminData")
public class AdminData {

    @Id
    private String id;

    private String username;
    private String password; //bcrypt
    private Long lastTimeActivity;


    public AdminData(String username, String password, Long lastTimeActivity) {
        this.username = username;
        this.password = password;
        this.lastTimeActivity = lastTimeActivity;
    }
    public AdminData(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Long getLastTimeActivity() {
        return lastTimeActivity;
    }

    public void setLastTimeActivity(Long lastTimeActivity) {
        this.lastTimeActivity = lastTimeActivity;
    }
}
