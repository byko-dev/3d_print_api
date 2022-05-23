package com.byko.api_3d_printing.model;

import com.byko.api_3d_printing.database.enums.User;

public class ConversationResponse {

    public String id;
    public String file;
    public String description;
    public User userType;
    public String downloadFileLink;
    public String data;
    public String fileName;
    public String username;

    public ConversationResponse(String id, String file, String description, User userType, String downloadFileLink,
                                String data, String fileName, String username) {
        this.id = id;
        this.file = file;
        this.description = description;
        this.userType = userType;
        this.downloadFileLink = downloadFileLink;
        this.data = data;
        this.fileName = fileName;
        this.username = username;
    }

    public ConversationResponse(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUserType() {
        return userType;
    }

    public void setUserType(User userType) {
        this.userType = userType;
    }

    public String getDownloadFileLink() {
        return downloadFileLink;
    }

    public void setDownloadFileLink(String downloadFileLink) {
        this.downloadFileLink = downloadFileLink;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
