package com.byko.api_3d_printing.model;

import org.springframework.core.io.Resource;

public class SelectedImage {

    public String id;
    public String title;
    public String description;
    public String date;
    public Resource imageResource;

    public SelectedImage(String id, String title, String description, String date, Resource imageResource) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.imageResource = imageResource;
    }

    public SelectedImage(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Resource getImageResource() {
        return imageResource;
    }

    public void setImageResource(Resource imageResource) {
        this.imageResource = imageResource;
    }
}
