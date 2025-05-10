package com.notesapp.notes_app.mapper;

import com.notesapp.notes_app.dto.NoteRequest;
import com.notesapp.notes_app.dto.NoteResponse;
import com.notesapp.notes_app.model.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    /**
     * Maps a Note entity to a NoteResponse DTO
     */
    public NoteResponse toNoteResponse(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());

        // Set folder information if the note belongs to a folder
        if (note.getFolder() != null) {
            response.setFolderId(note.getFolder().getId());
            response.setFolderName(note.getFolder().getName());
        }

        return response;
    }

    /**
     * Updates a Note entity from a NoteRequest DTO
     */
    public void updateNoteFromRequest(Note note, NoteRequest request) {
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        // Folder will be set separately since it requires a DB lookup
    }
}