package com.notesapp.notes_app.mapper;

import com.notesapp.notes_app.dto.FolderRequest;
import com.notesapp.notes_app.dto.FolderResponse;
import com.notesapp.notes_app.model.Folder;
import org.springframework.stereotype.Component;

@Component
public class FolderMapper {

    /**
     * Maps a Folder entity to a FolderResponse DTO
     */
    public FolderResponse toFolderResponse(Folder folder) {
        FolderResponse response = new FolderResponse();
        response.setId(folder.getId());
        response.setName(folder.getName());
        response.setCreatedAt(folder.getCreatedAt());
        response.setUpdatedAt(folder.getUpdatedAt());

        // Set parent folder information if exists
        if (folder.getParentFolder() != null) {
            response.setParentFolderId(folder.getParentFolder().getId());
            response.setParentFolderName(folder.getParentFolder().getName());
        }

//        // Count notes in folder
//        if (folder.getNotes() != null) {
//            response.setNoteCount(folder.getNotes().size());
//        } else {
//            response.setNoteCount(0);
//        }

        return response;
    }

    /**
     * Updates a Folder entity from a FolderRequest DTO
     */
    public void updateFolderFromRequest(Folder folder, FolderRequest request) {
        folder.setName(request.getName());
        // Parent folder will be set separately since it requires a DB lookup
    }
}