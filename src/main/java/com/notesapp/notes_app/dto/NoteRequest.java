package com.notesapp.notes_app.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for Note creation and update requests
 */
@Data
public class NoteRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String content;

    // Optional folder ID - can be null for notes at root level
    private Long folderId;

    // You can add more fields as needed for your note features
    // For example, if you want to support tags, formatting options, etc.
}