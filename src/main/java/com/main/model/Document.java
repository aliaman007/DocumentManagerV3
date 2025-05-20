package com.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Document {
    @Id @GeneratedValue
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Embedded
    private Metadata metadata;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    @Embeddable
    public static class Metadata {
        private String name;
        private String author;
        private String type;

        public Metadata() {}
        public Metadata(String name, String author) {
            this.name = name;
            this.author = author;
            this.type = "text";
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}