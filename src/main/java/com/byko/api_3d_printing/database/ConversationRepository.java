package com.byko.api_3d_printing.database;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends MongoRepository<ConversationData, String> {

    List<ConversationData> findByConversationId(String conversationId);
    void deleteAllByConversationId(String conversationId);
}
