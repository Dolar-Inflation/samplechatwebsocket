package com.messenger.samplechatwebsocket.Controllers;

import com.messenger.samplechatwebsocket.Entity.Message;
import com.messenger.samplechatwebsocket.Entity.Users;
import com.messenger.samplechatwebsocket.Repository.MessageRepository;
import com.messenger.samplechatwebsocket.Repository.UsersRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class MessageListRestController {
    private final MessageRepository messageRepository;
    private final UsersRepository usersRepository;


    public MessageListRestController(MessageRepository messageRepository, UsersRepository usersRepository) {
        this.messageRepository = messageRepository;
        this.usersRepository = usersRepository;
    }

    @GetMapping("history")
    public Iterable<Message> getHistory() {
        return messageRepository.findAll();
    }
    @GetMapping("users")
    public Iterable<Users> getUsers() {
        return usersRepository.findAll();
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        usersRepository.deleteById(id);
    }

}
