package com.example.svcstorage.controller;

import com.example.svcstorage.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = storageService.uploadFile(file);
        Map<String, String> response = new HashMap<>();
        response.put("filename", filename);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Map<String, String>> getFileUrl(@PathVariable String filename) {
        String url = storageService.getFileUrl(filename);
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        return ResponseEntity.ok(response);
    }
}
