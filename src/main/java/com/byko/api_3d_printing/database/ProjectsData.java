package com.byko.api_3d_printing.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "projectsData")
public class ProjectsData {

    @Id
    private String id;

    private String nameAndLastName;
    private String address;
    private String numberPhone;
    private String email;
    private String description;
    private String projectFile;
    private String downloadProjectFileLink;
    private String conversationKey;
    private String date;
    private Integer orderStatus;
    private String ipAddress;


    public ProjectsData(String nameAndLastName, String address, String numberPhone, String email, String description,
                        String projectFile, String downloadProjectFileLink, String conversationKey,
                        String date, Integer orderStatus, String ipAddress) {
        this.nameAndLastName = nameAndLastName;
        this.address = address;
        this.numberPhone = numberPhone;
        this.email = email;
        this.description = description;
        this.projectFile = projectFile;
        this.downloadProjectFileLink = downloadProjectFileLink;
        this.conversationKey = conversationKey;
        this.date = date;
        this.orderStatus = orderStatus;
        this.ipAddress = ipAddress;
    }

    public ProjectsData(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameAndLastName() {
        return nameAndLastName;
    }

    public void setNameAndLastName(String nameAndLastName) {
        this.nameAndLastName = nameAndLastName;
    }


    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(String projectFile) {
        this.projectFile = projectFile;
    }

    public String getConversationKey() {
        return conversationKey;
    }

    public void setConversationKey(String conversationKey) {
        this.conversationKey = conversationKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDownloadProjectFileLink() {
        return downloadProjectFileLink;
    }

    public void setDownloadProjectFileLink(String downloadProjectFileLink) {
        this.downloadProjectFileLink = downloadProjectFileLink;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
