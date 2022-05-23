package com.byko.api_3d_printing.database;

import com.byko.api_3d_printing.database.enums.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "conversationData")
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

    public ConversationData(String conversationId, String description, String file, String downloadFileLink,
                            String data, User userType, String ipAddress) {
        this.conversationId = conversationId;
        this.description = description;
        this.file = file;
        this.downloadFileLink = downloadFileLink;
        this.data = data;
        this.userType = userType;
        this.ipAddress = ipAddress;
    }

    public ConversationData(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDownloadFileLink() {
        return downloadFileLink;
    }

    public void setDownloadFileLink(String downloadFileLink) {
        this.downloadFileLink = downloadFileLink;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public User getUserType() {
        return userType;
    }

    public void setUserType(User userType) {
        this.userType = userType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
