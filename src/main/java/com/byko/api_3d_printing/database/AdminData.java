package com.byko.api_3d_printing.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "adminData")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminData {

    @Id
    private String id;

    private String username;
    private String password; //bcrypt
    private Long lastTimeActivity;
}
