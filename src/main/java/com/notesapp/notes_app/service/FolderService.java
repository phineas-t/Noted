package com.notesapp.notes_app.service;

import com.notesapp.notes_app.dto.FolderRequest;
import com.notesapp.notes_app.dto.FolderResponse;
import com.notesapp.notes_app.mapper.FolderMapper;
import com.notesapp.notes_app.model.Folder;
import com.notesapp.notes_app.model.User;
import com.notesapp.notes_app.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    public FolderMapper folderMapper;

    /**
     * Get all root folders for the current user
     * @return List of folders at the root level
     */
    public List<FolderResponse> getRootFolders() {
        List<Folder> folders = folderRepository.findByUserIdAndParentFolderIsNull(userService.getCurrentUserId());
        return folders.stream()
                .map(folderMapper::toFolderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all subfolders within a specific folder
     * @param parentFolderId ID of the parent folder
     * @return List of subfolders
     */
    public List<FolderResponse> getSubfolders(Long parentFolderId) {
        // Verify parent folder exists and belongs to current user
        if (!folderRepository.findByIdAndUserId(parentFolderId, userService.getCurrentUserId()).isPresent()) {
            throw new RuntimeException("Parent folder not found or access denied");
        }

        List<Folder> folders = folderRepository.findByUserIdAndParentFolderId(
                userService.getCurrentUserId(), parentFolderId);

        return folders.stream()
                .map(folderMapper::toFolderResponse)
                .collect(Collectors.toList());
    }



    /**
     * Get a specific folder for the current user
     * @param folderId Folder ID
     * @return Optional containing the folder if found
     */
    public Optional<Folder> getFolderForCurrentUser(Long folderId) {
        if (folderId == null) {
            return Optional.empty();
        }
        return folderRepository.findByIdAndUserId(folderId, userService.getCurrentUserId());
    }

    /**
     * Create a new folder
     * @param folderRequest Folder details
     * @return Created folder
     */
    public FolderResponse createFolder(FolderRequest folderRequest) {
        User currentUser = userService.getCurrentUser();

        // Check if folder with same name already exists at this level
        if (folderRepository.existsByNameAndUserIdAndParentFolderId(
                folderRequest.getName(),
                currentUser.getId(),
                folderRequest.getParentFolderId())) {
            throw new RuntimeException("A folder with this name already exists at this location");
        }

        Folder folder = new Folder();
        folder.setName(folderRequest.getName());
        folder.setUser(currentUser);
        folder.setCreatedAt(LocalDateTime.now());
        folder.setUpdatedAt(LocalDateTime.now());

        // Set parent folder if provided
        if (folderRequest.getParentFolderId() != null) {
            Optional<Folder> parentFolder = getFolderForCurrentUser(folderRequest.getParentFolderId());
            if (parentFolder.isPresent()) {
                folder.setParentFolder(parentFolder.get());
            } else {
                throw new RuntimeException("Parent folder not found or access denied");
            }
        }

        Folder savedFolder = folderRepository.save(folder);
        return folderMapper.toFolderResponse(savedFolder);
    }



    /**
     * Update an existing folder
     * @param folderId Folder ID
     * @param folderRequest Updated folder details
     * @return Updated folder
     */
    public FolderResponse updateFolder(Long folderId, FolderRequest folderRequest) {
        Optional<Folder> folderOpt = getFolderForCurrentUser(folderId);
        if (folderOpt.isEmpty()) {
            throw new RuntimeException("Folder not found or access denied");
        }

        Folder folder = folderOpt.get();

        // Check if new name would conflict with existing folder at same level
        // Only check if name is actually changing
        if (!folder.getName().equals(folderRequest.getName())) {
            Long parentFolderId = folder.getParentFolder() != null ?
                    folder.getParentFolder().getId() : null;

            if (folderRepository.existsByNameAndUserIdAndParentFolderId(
                    folderRequest.getName(),
                    userService.getCurrentUserId(),
                    parentFolderId)) {
                throw new RuntimeException("A folder with this name already exists at this location");
            }
        }

        // Update folder properties
        folder.setName(folderRequest.getName());
        folder.setUpdatedAt(LocalDateTime.now());

        // Update parent folder if changed
        if (folderRequest.getParentFolderId() != null) {
            // Prevent setting a folder as its own parent
            if (folderRequest.getParentFolderId().equals(folderId)) {
                throw new RuntimeException("A folder cannot be its own parent");
            }

            Optional<Folder> newParentOpt = getFolderForCurrentUser(folderRequest.getParentFolderId());
            if (newParentOpt.isEmpty()) {
                throw new RuntimeException("Parent folder not found or access denied");
            }

            folder.setParentFolder(newParentOpt.get());
        } else {
            // Move to root level if parentFolderId is null
            folder.setParentFolder(null);
        }

        Folder savedFolder = folderRepository.save(folder);
        return folderMapper.toFolderResponse(savedFolder);
    }

    /**
     * Delete a folder
     * @param folderId Folder ID
     */
    public void deleteFolder(Long folderId) {
        Optional<Folder> folderOpt = getFolderForCurrentUser(folderId);
        if (folderOpt.isEmpty()) {
            throw new RuntimeException("Folder not found or access denied");
        }

        folderRepository.delete(folderOpt.get());
    }
}