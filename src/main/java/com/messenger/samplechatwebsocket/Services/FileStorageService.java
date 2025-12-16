package com.messenger.samplechatwebsocket.Services;

import com.messenger.samplechatwebsocket.Entity.FileEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public FileStorageService() throws IOException {
        Files.createDirectories(root);
    }

    public FileEntity store(MultipartFile file,String uploader) throws IOException {
        String fileName = System.currentTimeMillis() +"_"+Path.of(file.getOriginalFilename()).getFileName();
        Path target = root.resolve(fileName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        FileEntity e = new FileEntity();
        e.setOriginalName(file.getOriginalFilename());
        e.setMimeType(file.getContentType());
        e.setSize(file.getSize());
        e.setStoragePath(target.toString());
        e.setUploader(uploader);
        e.setCreatedAt(Instant.now());
        return e;




    }

    public Resource loadAsResource(FileEntity fileEntity){
        return new FileSystemResource(Paths.get(fileEntity.getStoragePath()));
    }
}
