package com.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.main.model.Document;
import com.main.service.DocumentService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @Operation(summary = "Upload a document")
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Document> upload(@RequestParam("file") MultipartFile file,
                                          @RequestParam String author) {
        return ResponseEntity.ok(documentService.upload(file, author));
    }

    @Operation(summary = "Search documents by query")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<Document>> search(
            @RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(documentService.search(query, pageable));
    }

    @Operation(summary = "Filter documents by metadata")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<Document>> filter(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type,
            Pageable pageable) {
        return ResponseEntity.ok(documentService.filter(author, type, pageable));
    }
}