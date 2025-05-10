package com.notesapp.notes_app.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FolderResponse {
    private Long id;
    private String name;
    private Long parentFolderId;
    private String parentFolderName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FolderResponse> subfolders = new ArrayList<>();
    private List<NoteResponse> notes = new ArrayList<>();
}
