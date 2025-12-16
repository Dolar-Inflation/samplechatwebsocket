package com.messenger.samplechatwebsocket.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Entity
@Getter
@Setter
public class FileEntity {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

   private Long id;

    private String originalName;
    private String mimeType;
    private Long size;
    private String storagePath;
    private String uploader;
    private Instant createdAt;



}
