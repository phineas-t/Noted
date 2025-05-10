package com.notesapp.notes_app.controller;

import com.notesapp.notes_app.dto.ApiResponse;
import com.notesapp.notes_app.dto.FolderRequest;
import com.notesapp.notes_app.dto.FolderResponse;
import com.notesapp.notes_app.service.FolderService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling Folder related operations
 */
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    /**
     * Get all root folders for the current user
     * @return List of root folders
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> getRootFolders() {
        List<FolderResponse> folders = folderService.getRootFolders();
        return ResponseEntity.ok(ApiResponse.success("Root folders retrieved successfully", folders));
    }

    /**
     * Get all subfolders within a specific folder
     * @param parentId The ID of the parent folder
     * @return List of subfolders
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<FolderResponse>>> getSubfolders(@PathVariable Long parentId) {
        try {
            List<FolderResponse> folders = folderService.getSubfolders(parentId);
            return ResponseEntity.ok(ApiResponse.success("Subfolders retrieved successfully", folders));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Create a new folder
     * @param folderRequest Folder details
     * @return Created folder
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FolderResponse>> createFolder(@Valid @RequestBody FolderRequest folderRequest) {
        try {
            FolderResponse folder = folderService.createFolder(folderRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Folder created successfully", folder));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get a specific folder
     * @param id Folder ID
     * @return Folder if found and belongs to current user
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FolderResponse>> getFolderById(@PathVariable Long id) {
        return folderService.getFolderForCurrentUser(id)
                .map(folder -> {
                    FolderResponse response = folderService.folderMapper.toFolderResponse(folder);
                    return ResponseEntity.ok(ApiResponse.success("Folder retrieved successfully", response));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Folder not found or access denied")));
    }

    /**
     * Update a folder
     * @param id Folder ID
     * @param folderRequest Updated folder details
     * @return Updated folder
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FolderResponse>> updateFolder(
            @PathVariable Long id,
            @Valid @RequestBody FolderRequest folderRequest) {

        try {
            FolderResponse updatedFolder = folderService.updateFolder(id, folderRequest);
            return ResponseEntity.ok(ApiResponse.success("Folder updated successfully", updatedFolder));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete a folder
     * @param id Folder ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(@PathVariable Long id) {
        try {
            folderService.deleteFolder(id);
            return ResponseEntity.ok(ApiResponse.success("Folder deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}