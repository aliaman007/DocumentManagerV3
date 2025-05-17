package com.main.service;

import com.main.model.Document;
import com.main.repository.*;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {
    @Autowired private DocumentRepository documentRepository;
    private final Tika tika = new Tika();

    public Document upload(MultipartFile file) {
        try {
            String content = tika.parseToString(file.getInputStream());
            Document doc = new Document();
            doc.setContent(content);
            doc.setMetadata(new Document.Metadata(file.getOriginalFilename(), "author"));
            return documentRepository.save(doc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    public Page<Document> search(String query, Pageable pageable) {
        return documentRepository.findByContentContaining(query, pageable);
    }

    public Page<Document> filter(String author, String type, Pageable pageable) {
        return documentRepository.findByMetadata(author, type, pageable);
    }
}