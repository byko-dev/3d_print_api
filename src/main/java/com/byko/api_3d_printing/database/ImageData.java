package com.byko.api_3d_printing.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "imagesData")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageData {

    @Id
    private String id;
    private String imageFileName;
    private String imageAlt;
    private String title;
    private String description;
    private String date;
}
