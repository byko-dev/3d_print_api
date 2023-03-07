package com.byko.api_3d_printing.model;

import com.byko.api_3d_printing.database.MessageDAO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MessageDTOMapper implements Function<MessageDAO, MessageDTO> {

    @Override
    public MessageDTO apply(MessageDAO messageData) {
        return new MessageDTO(messageData.getId(),
                messageData.getFileId(),
                messageData.getDescription(),
                messageData.getUserType(), messageData.getData(), messageData.getFileName(), "");
    }
}
