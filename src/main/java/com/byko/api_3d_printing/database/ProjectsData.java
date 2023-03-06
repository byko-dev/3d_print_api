package com.byko.api_3d_printing.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "projectsData")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectsData {

    @Id
    private String id;

    private String nameAndLastName;
    private String address;
    private String numberPhone;
    private String email;
    private String description;
    private String projectFileId;
    private String conversationKey;
    private String date;
    private Integer orderStatus;
    private String ipAddress;
    private String fileName;
}
