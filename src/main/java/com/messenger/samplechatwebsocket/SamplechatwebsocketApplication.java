package com.messenger.samplechatwebsocket;

import com.messenger.samplechatwebsocket.Entity.Users;
import com.messenger.samplechatwebsocket.Repository.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SamplechatwebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamplechatwebsocketApplication.class, args);
    }
    @Bean
    CommandLineRunner init(UsersRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("root").isEmpty()) {
                Users root = new Users();
                root.setUsername("root");
                root.setPassword(encoder.encode("root"));
                root.setRole("admin");
                repo.save(root);
            }
        };
    }

}
