package com.byko.api_3d_printing.database;

import com.byko.api_3d_printing.database.enums.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "conversationData")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConversationData {

    @Id
    private String id;

    private String conversationId;
    private String description;
    private String file;
    private String downloadFileLink;
    private String data;
    private User userType;
    private String ipAddress;
}
