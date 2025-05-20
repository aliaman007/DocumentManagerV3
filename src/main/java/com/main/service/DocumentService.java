package com.main.service;

import com.main.model.Document;
import com.main.repository.DocumentRepository;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private BatchService batchService;

    public Document upload(MultipartFile file, String author) {
        try {
            logger.info("Processing file: {}", file.getOriginalFilename());
            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            parser.parse(file.getInputStream(), handler, metadata, context);
            String content = handler.toString();
            logger.debug("Extracted content length: {}", content.length());
            Document doc = new Document();
            doc.setContent(content);
            doc.setMetadata(new Document.Metadata(file.getOriginalFilename(), author));
            Document savedDoc = documentRepository.save(doc);
            try {
				batchService.processDocument(savedDoc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            logger.info("Successfully uploaded document: {}", savedDoc.getId());
            return savedDoc;
        } catch (IOException | TikaException e) {
            logger.error("Failed to process file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to process file: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error processing file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Unexpected error processing file: " + e.getMessage(), e);
        }
    }

    @Cacheable(value = "searchCache", key = "#query + #pageable.pageNumber")
    public Page<Document> search(String query, Pageable pageable) {
        return documentRepository.findByContentContaining(query, pageable);
    }

    public Page<Document> filter(String author, String type, Pageable pageable) {
        return documentRepository.findByMetadata(author, type, pageable);
    }
}