package com.messenger.samplechatwebsocket.Controllers;

import com.messenger.samplechatwebsocket.DTO.UsersDTO;
import com.messenger.samplechatwebsocket.Entity.Users;
import com.messenger.samplechatwebsocket.Mapper.EntityToDTO;
import com.messenger.samplechatwebsocket.Repository.UsersRepository;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {


    private final EntityToDTO entityToDTO ;
    private final UsersRepository usersRepository;

    public AuthController(EntityToDTO entityToDTO,  UsersRepository usersRepository1) {
        this.entityToDTO = entityToDTO;



        this.usersRepository = usersRepository1;

    }


    @GetMapping("/")
    public String auth(){
        return "redirect:/auth.html";
    }
    @MessageMapping("/RegisterUser")
    @SendTo("/topic/messages")
    public UsersDTO registerUser(UsersDTO usersDTO){

        System.out.println(usersDTO);
        Users users = (Users) entityToDTO.toEntity(usersDTO,Users.class);
        usersRepository.save(users);
        return usersDTO;
    }

    @MessageMapping("/LoginUser")
    @SendTo("/topic/messages")
    public UsersDTO loginUser(UsersDTO usersDTO) {
        System.out.println("Login attempt: " + usersDTO);

        Users user = usersRepository.findByUsername(usersDTO.getUsername());
        if (user == null) {
            return new UsersDTO("ERROR: user not found", null);
        }

        if (!user.getPassword().equals(usersDTO.getPassword())) {
            return new UsersDTO("ERROR: wrong password", null);
        }


        return new UsersDTO(user.getUsername(), null);
    }


}
