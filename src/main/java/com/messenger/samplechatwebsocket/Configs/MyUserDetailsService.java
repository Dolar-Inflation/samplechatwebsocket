package com.messenger.samplechatwebsocket.Configs;

import com.messenger.samplechatwebsocket.Entity.Users;
import com.messenger.samplechatwebsocket.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = usersRepository.findByUsername(username);


        return user.map(MyUserDetails::new)
                .orElseThrow(()->new UsernameNotFoundException(username+"There is not such user in REPO"));
    }
}
