package com.notesapp.notes_app.repository;

import com.notesapp.notes_app.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserIdAndParentFolderIsNull(Long userId);
    List<Folder> findByUserIdAndParentFolderId(Long userId, Long parentFolderId);
    Optional<Folder> findByIdAndUserId(Long id, Long userId);
    boolean existsByNameAndUserIdAndParentFolderId(String name, Long userId, Long parentFolderId);
}
