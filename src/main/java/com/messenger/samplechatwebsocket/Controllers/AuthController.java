package com.messenger.samplechatwebsocket.Controllers;

import com.messenger.samplechatwebsocket.Configs.MyUserDetails;
import com.messenger.samplechatwebsocket.DTO.UsersDTO;
import com.messenger.samplechatwebsocket.Entity.Users;
import com.messenger.samplechatwebsocket.Mapper.EntityToDTO;
import com.messenger.samplechatwebsocket.Repository.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;

@Controller
public class AuthController {

    private final EntityToDTO entityToDTO;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(EntityToDTO entityToDTO,
                          UsersRepository usersRepository,
                          PasswordEncoder passwordEncoder) {
        this.entityToDTO = entityToDTO;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/start")
    public String auth() {
        return "redirect:/auth.html";
    }
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody UsersDTO usersDTO, HttpServletRequest request) {
        Users user = usersRepository.findByUsername(usersDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(usersDTO.getPassword(), user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UsersDTO("ERROR", null));
        }
        MyUserDetails userDetails = new MyUserDetails(user);


        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(new UsersDTO(user.getUsername(), null));
    }
    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody UsersDTO usersDTO, HttpServletRequest request) {
        Users users = (Users) entityToDTO.toEntity(usersDTO, Users.class);
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        usersRepository.save(users);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                users.getUsername(), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(new UsersDTO(users.getUsername(), null));

    }


}
