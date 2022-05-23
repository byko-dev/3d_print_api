package com.byko.api_3d_printing.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "imagesData")
public class ImageData {

    @Id
    private String id;
    private String imageFileName;
    private String imageAlt;
    private String title;
    private String description;
    private String date;

    public ImageData(String imageFileName, String imageAlt, String title, String description, String date) {
        this.imageFileName = imageFileName;
        this.imageAlt = imageAlt;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public ImageData() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
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

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }
}
