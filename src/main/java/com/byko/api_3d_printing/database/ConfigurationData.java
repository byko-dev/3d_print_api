package com.byko.api_3d_printing.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "configuration")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigurationData {

    @Id
    private String id;
    private String email;
    private String emailPass;
    private boolean emailEnable;
}
