package com.praveen.controller;

import com.praveen.entities.ChatFile;
import com.praveen.repository.ChatFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final ChatFileRepository repository;

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        ChatFile chatFile = ChatFile.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .size(file.getSize())
                .data(file.getBytes())
                .build();

        ChatFile savedFile = repository.save(chatFile);

        return ResponseEntity.ok(savedFile.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {

        ChatFile file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getData());
    }
}