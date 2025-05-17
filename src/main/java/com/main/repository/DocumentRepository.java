package com.main.repository;

import com.main.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query("SELECT d FROM Document d WHERE d.content ILIKE %:query%")
    Page<Document> findByContentContaining(String query, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE (:author IS NULL OR d.metadata.author = :author) AND (:type IS NULL OR d.metadata.type = :type)")
    Page<Document> findByMetadata(String author, String type, Pageable pageable);
}