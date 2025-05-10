package com.notesapp.notes_app.repository;

import com.notesapp.notes_app.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);

    // Find notes in a specific folder
    List<Note> findByFolderIdAndUserId(Long folderId, Long userId);

    // Find notes that aren't in any folder (root level notes)
    List<Note> findByFolderIsNullAndUserId(Long userId);
}