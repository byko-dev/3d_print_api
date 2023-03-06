package com.byko.api_3d_printing.database.repository;

import com.byko.api_3d_printing.database.MessageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<MessageData, String> {

    List<MessageData> findByConversationId(String conversationId);
    void deleteAllByConversationId(String conversationId);
}
