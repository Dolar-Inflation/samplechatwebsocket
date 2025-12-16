package com.messenger.samplechatwebsocket.Controllers;

import com.messenger.samplechatwebsocket.Entity.FileEntity;
import com.messenger.samplechatwebsocket.Repository.FileRepository;
import com.messenger.samplechatwebsocket.Services.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;


    public FileController(FileRepository fileRepository, FileStorageService fileStorageService, SimpMessagingTemplate messagingTemplate) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
        this.messagingTemplate = messagingTemplate;
    }


    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }
        FileEntity saved = fileStorageService.store(file,principal.getName());
        fileRepository.save(saved);

        Map<String, Object> payload = Map.of(
                "type", "file",
                "fileId", saved.getId(),
                "filename", saved.getOriginalName(),
                "size", saved.getSize(),
                "uploader", saved.getUploader(),
                "url", "/api/files/" + saved.getId()
        );
        messagingTemplate.convertAndSend("/topic/messages", (Object) payload);

        return ResponseEntity.ok(Map.of("fileId", saved.getId()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id, Principal principal) {
        FileEntity file = fileRepository.findById(id).orElse(null);

        Resource resource = fileStorageService.loadAsResource(file);
        String contentType = file.getMimeType() == null ? "application/octet-stream" : file.getMimeType();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
