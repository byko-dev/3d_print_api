package com.byko.api_3d_printing.services;

import com.byko.api_3d_printing.database.MessageData;
import com.byko.api_3d_printing.database.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private MessageRepository messageRepository;

    public void deleteAllByConversationId(String conversationId){
        messageRepository.deleteAllByConversationId(conversationId);
    }

    public void save(MessageData conversationData){
        messageRepository.save(conversationData);
    }

    public List<MessageData> getByConversationId(String id){
        return messageRepository.findByConversationId(id);
    }


}
