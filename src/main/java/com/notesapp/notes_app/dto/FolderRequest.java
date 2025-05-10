package com.notesapp.notes_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FolderRequest {
    @NotBlank(message = "Folder name cannot be empty")
    private String name;
    private Long parentFolderId;
}
