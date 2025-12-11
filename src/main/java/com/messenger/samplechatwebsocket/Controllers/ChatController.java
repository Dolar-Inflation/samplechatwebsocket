package com.messenger.samplechatwebsocket.Controllers;


import com.messenger.samplechatwebsocket.DTO.MessageDTO;
import com.messenger.samplechatwebsocket.Entity.Message;
import com.messenger.samplechatwebsocket.Mapper.EntityToDTO;
import com.messenger.samplechatwebsocket.Repository.MessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

private final MessageRepository messageRepository;
private final EntityToDTO entityToDTO;

    public ChatController(MessageRepository messageRepository, EntityToDTO entityToDTO) {
        this.messageRepository = messageRepository;
        this.entityToDTO = entityToDTO;
    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public MessageDTO getMessages(MessageDTO message) {
        System.out.println(message);
        Message entity = entityToDTO.toEntity(message);
        messageRepository.save(entity);

        return message;
    }

}
