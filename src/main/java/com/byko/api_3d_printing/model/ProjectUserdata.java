package com.byko.api_3d_printing.model;

import com.byko.api_3d_printing.database.ProjectsData;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class ProjectUserdata {

    public String getNameAndLastName() {
        return nameAndLastName;
    }

    public void setNameAndLastName(String nameAndLastName) {
        this.nameAndLastName = nameAndLastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public ProjectUserdata(String nameAndLastName, String address, String numberPhone, String email, String date, Integer orderStatus) {
        this.nameAndLastName = nameAndLastName;
        this.address = address;
        this.numberPhone = numberPhone;
        this.email = email;
        this.date = date;
        this.orderStatus = orderStatus;
    }

    private String nameAndLastName;
    private String address;
    private String numberPhone;
    private String email;
    private String date;
    private Integer orderStatus;

}
