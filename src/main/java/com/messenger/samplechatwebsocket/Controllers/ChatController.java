package com.messenger.samplechatwebsocket.Controllers;


import com.messenger.samplechatwebsocket.DTO.MessageDTO;
import com.messenger.samplechatwebsocket.Entity.Message;
import com.messenger.samplechatwebsocket.Mapper.EntityToDTO;
import com.messenger.samplechatwebsocket.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
public class ChatController {
@Autowired
private final MessageRepository messageRepository;
private final EntityToDTO entityToDTO;


    public ChatController(MessageRepository messageRepository, EntityToDTO entityToDTO) {
        this.messageRepository = messageRepository;
        this.entityToDTO = entityToDTO;

    }

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public MessageDTO getMessages(MessageDTO messageDTO) {
        System.out.println(messageDTO);
        Message entity = (Message) entityToDTO.toEntity(messageDTO,Message.class);
        messageRepository.save(entity);

        return messageDTO;
    }
    @MessageMapping("/addUser")
    @SendTo("/topic/messages")
    public MessageDTO addUser(MessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        // Add username in web socket session
        System.out.println(chatMessage);
        Message entity = (Message) entityToDTO.toEntity(chatMessage,Message.class);
        messageRepository.save(entity);
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getUsername());
        return chatMessage;

    }
}
