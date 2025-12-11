package com.messenger.samplechatwebsocket.Repository;

import com.messenger.samplechatwebsocket.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
