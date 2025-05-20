package com.main.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentTests {

    @Test
    void testDocumentGettersAndSetters() {
        Document document = new Document();
        document.setId(1L);
        document.setContent("Test content");

        Document.Metadata metadata = new Document.Metadata("test.pdf", "John Doe");
        document.setMetadata(metadata);

        assertEquals(1L, document.getId());
        assertEquals("Test content", document.getContent());
        assertEquals("test.pdf", document.getMetadata().getName());
        assertEquals("John Doe", document.getMetadata().getAuthor());
        assertEquals("text", document.getMetadata().getType());
    }

    @Test
    void testMetadataConstructor() {
        Document.Metadata metadata = new Document.Metadata("doc.pdf", "Jane Doe");
        assertEquals("doc.pdf", metadata.getName());
        assertEquals("Jane Doe", metadata.getAuthor());
        assertEquals("text", metadata.getType());
    }
}