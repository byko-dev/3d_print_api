package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.ConversationData;
import com.byko.api_3d_printing.database.repository.ConversationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConversationService {

    private ConversationRepository conversationRepository;

    public void deleteAllByConversationId(String conversationId){
        conversationRepository.deleteAllByConversationId(conversationId);
    }

    public void save(ConversationData conversationData){
        conversationRepository.save(conversationData);
    }

    public List<ConversationData> getByConversationId(String id){
        return conversationRepository.findByConversationId(id);
    }


}
