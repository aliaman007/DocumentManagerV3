package com.main.controller;

import com.main.model.Document;
import com.main.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequestMapping("/api/documents")
public class DocumentController {
    @Autowired private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(documentService.upload(file));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Document>> search(
            @RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(documentService.search(query, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<Document>> filter(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type,
            Pageable pageable) {
        return ResponseEntity.ok(documentService.filter(author, type, pageable));
    }
}