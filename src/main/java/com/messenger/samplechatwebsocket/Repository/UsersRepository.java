package com.messenger.samplechatwebsocket.Repository;


import com.messenger.samplechatwebsocket.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
}
