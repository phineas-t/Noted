package com.notesapp.notes_app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Note responses
 */
@Data
public class NoteResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Folder information - will be null for notes at root level
    private Long folderId;
    private String folderName;

    // You can add more fields based on your requirements
    // For example: tags, attachment info, formatting metadata, etc.
}