package com.main.controller;

import com.main.model.Document;
import com.main.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUploadDocumentSuccess() throws Exception {
        Document document = new Document();
        document.setMetadata(new Document.Metadata("test.pdf", "John Doe"));
        when(documentService.saveDocument(any(), eq("John Doe"))).thenReturn(document);

        mockMvc.perform(multipart("/api/documents/upload")
                .file("file", "test content".getBytes())
                .param("author", "John Doe")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.name").value("test.pdf"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUploadDocumentForbidden() throws Exception {
        mockMvc.perform(multipart("/api/documents/upload")
                .file("file", "test content".getBytes())
                .param("author", "John Doe")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testSearchDocuments() throws Exception {
        Page<Document> page = new PageImpl<>(new ArrayList<>());
        when(documentService.searchDocuments(eq("test"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/documents/search")
                .param("query", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }
}