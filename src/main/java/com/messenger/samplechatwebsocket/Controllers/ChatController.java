package com.messenger.samplechatwebsocket.Controllers;


import com.messenger.samplechatwebsocket.Entity.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {



    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message getMessages(Message message) {
        System.out.println(message);
        return message;
    }

}
