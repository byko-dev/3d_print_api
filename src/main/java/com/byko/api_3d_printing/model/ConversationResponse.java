package com.byko.api_3d_printing.model;

import com.byko.api_3d_printing.database.enums.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConversationResponse {

    public String id;
    public String fileId;
    public String description;
    public User userType;
    public String data;
    public String fileName;
    public String username;

}
