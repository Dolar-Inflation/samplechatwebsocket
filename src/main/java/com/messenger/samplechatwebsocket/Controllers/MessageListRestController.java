package com.messenger.samplechatwebsocket.Controllers;

import com.messenger.samplechatwebsocket.Entity.Message;
import com.messenger.samplechatwebsocket.Repository.MessageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageListRestController {
    private final MessageRepository messageRepository;


    public MessageListRestController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("history")
    public Iterable<Message> getHistory() {
        return messageRepository.findAll();
    }

}
