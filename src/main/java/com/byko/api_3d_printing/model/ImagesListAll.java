package com.byko.api_3d_printing.model;

public class ImagesListAll {

    public String title;
    public String date;
    public String id;

    public ImagesListAll(String title, String date, String id) {
        this.title = title;
        this.date = date;
        this.id = id;
    }

    public ImagesListAll(){}


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
