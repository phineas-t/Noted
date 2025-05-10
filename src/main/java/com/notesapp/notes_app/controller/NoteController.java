//package com.notesapp.notes_app.controller;
//
//import com.notesapp.notes_app.dto.ApiResponse;
//import com.notesapp.notes_app.model.Note;
//import com.notesapp.notes_app.model.User;
//import com.notesapp.notes_app.repository.NoteRepository;
//import com.notesapp.notes_app.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * REST Controller for handling Note related operations
// */
//@RestController // Marks this class as a controller where every method returns a domain object instead of a view
//@RequestMapping("/api/notes") // Base URL mapping for all endpoints in this controller
//public class NoteController {
//
//    @Autowired // Injects the NoteRepository dependency
//    private NoteRepository noteRepository;
//
//    @Autowired // Injects the UserService dependency
//    private UserService userService;
//
//    /**
//     * Retrieves all notes belonging to the currently authenticated user
//     * @return List of notes for the current user
//     */
//    @GetMapping // Maps HTTP GET requests to this method at the base URL
//    public ResponseEntity<ApiResponse<List<Note>>> getNotes() {
//        // Get current user ID from the UserService
//        List<Note> notes = noteRepository.findByUserId(userService.getCurrentUserId());
//
//        // Return notes with HTTP 200 OK status and success message
//        return ResponseEntity.ok(ApiResponse.success("Notes retrieved successfully", notes));
//    }
//
//    /**
//     * Creates a new note for the currently authenticated user
//     * @param note The note object from request body
//     * @return The created note with ID
//     */
//    @PostMapping // Maps HTTP POST requests to this method at the base URL
//    public ResponseEntity<ApiResponse<Note>> createNote(@RequestBody Note note) {
//        // Get the complete user object of currently authenticated user
//        User currentUser = userService.getCurrentUser();
//
//        // Associate the note with the current user
//        note.setUser(currentUser);
//
//        // Set creation and update timestamps to current time
//        note.setCreatedAt(LocalDateTime.now());
//        note.setUpdatedAt(LocalDateTime.now());
//
//        // Save the note to database
//        Note savedNote = noteRepository.save(note);
//
//        // Return the saved note with HTTP 201 CREATED status
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success("Note created successfully", savedNote));
//    }
//
//    /**
//     * Retrieves a specific note by ID
//     * @param id The ID of the note to retrieve
//     * @return The requested note if it belongs to the current user
//     */
//    @GetMapping("/{id}") // Maps HTTP GET requests for a specific note ID
//    public ResponseEntity<ApiResponse<Note>> getNoteById(@PathVariable Long id) {
//        try {
//            // Find the note by ID or throw exception if not found
//            Note note = noteRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Note not found"));
//
//            // Security check: verify that the note belongs to the current user
//            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
//                // Return HTTP 403 FORBIDDEN if note doesn't belong to current user
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(ApiResponse.error("Access denied"));
//            }
//
//            // Return the note with HTTP 200 OK status
//            return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", note));
//        } catch (RuntimeException e) {
//            // Handle not found exception with HTTP 404 NOT FOUND status
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    /**
//     * Updates an existing note
//     * @param id The ID of the note to update
//     * @param noteDetails The updated note details
//     * @return The updated note
//     */
//    @PutMapping("/{id}") // Maps HTTP PUT requests for a specific note ID
//    public ResponseEntity<ApiResponse<Note>> updateNote(@PathVariable Long id, @RequestBody Note noteDetails) {
//        try {
//            // Find the note by ID or throw exception if not found
//            Note note = noteRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Note not found"));
//
//            // Security check: verify that the note belongs to the current user
//            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
//                // Return HTTP 403 FORBIDDEN if note doesn't belong to current user
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(ApiResponse.error("Access denied"));
//            }
//
//            // Update the note fields with new values
//            note.setTitle(noteDetails.getTitle());
//            note.setContent(noteDetails.getContent());
//            note.setUpdatedAt(LocalDateTime.now()); // Update the modification timestamp
//
//            // Save the updated note to database
//            Note updatedNote = noteRepository.save(note);
//
//            // Return the updated note with HTTP 200 OK status
//            return ResponseEntity.ok(ApiResponse.success("Note updated successfully", updatedNote));
//        } catch (RuntimeException e) {
//            // Handle not found exception with HTTP 404 NOT FOUND status
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(ApiResponse.error(e.getMessage()));
//        }
//    }
//
//    /**
//     * Deletes a note by ID
//     * @param id The ID of the note to delete
//     * @return Success message if deleted successfully
//     */
//    @DeleteMapping("/{id}") // Maps HTTP DELETE requests for a specific note ID
//    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
//        try {
//            // Find the note by ID or throw exception if not found
//            Note note = noteRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Note not found"));
//
//            // Security check: verify that the note belongs to the current user
//            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
//                // Return HTTP 403 FORBIDDEN if note doesn't belong to current user
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(ApiResponse.error("Access denied"));
//            }
//
//            // Delete the note from database
//            noteRepository.delete(note);
//
//            // Return success message with HTTP 200 OK status
//            return ResponseEntity.ok(ApiResponse.success("Note deleted successfully", null));
//        } catch (RuntimeException e) {
//            // Handle not found exception with HTTP 404 NOT FOUND status
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(ApiResponse.error(e.getMessage()));
//        }
//    }
//}


package com.notesapp.notes_app.controller;

import com.notesapp.notes_app.dto.ApiResponse;
import com.notesapp.notes_app.dto.NoteRequest;
import com.notesapp.notes_app.dto.NoteResponse;
import com.notesapp.notes_app.mapper.NoteMapper;
import com.notesapp.notes_app.model.Folder;
import com.notesapp.notes_app.model.Note;
import com.notesapp.notes_app.model.User;
import com.notesapp.notes_app.repository.NoteRepository;
import com.notesapp.notes_app.service.FolderService;
import com.notesapp.notes_app.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for handling Note related operations
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private NoteMapper noteMapper;

    /**
     * Retrieves all notes belonging to the currently authenticated user
     * @return List of note DTOs for the current user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getAllNotes() {
        List<Note> notes = noteRepository.findByUserId(userService.getCurrentUserId());

        // Convert entities to DTOs
        List<NoteResponse> noteResponses = notes.stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Notes retrieved successfully", noteResponses));
    }

    /**
     * Get notes that aren't in any folder (root level)
     * @return List of root level notes
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getRootNotes() {
        List<Note> notes = noteRepository.findByFolderIsNullAndUserId(userService.getCurrentUserId());

        List<NoteResponse> noteResponses = notes.stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Root notes retrieved successfully", noteResponses));
    }

    /**
     * Creates a new note for the currently authenticated user
     * @param noteRequest The note request DTO from request body
     * @return The created note as a response DTO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> createNote(@Valid @RequestBody NoteRequest noteRequest) {
        User currentUser = userService.getCurrentUser();

        // Create and set up new note entity
        Note note = new Note();
        noteMapper.updateNoteFromRequest(note, noteRequest);
        note.setUser(currentUser);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        // Set folder if provided
        if (noteRequest.getFolderId() != null) {
            Optional<Folder> folder = folderService.getFolderForCurrentUser(noteRequest.getFolderId());
            if (folder.isPresent()) {
                note.setFolder(folder.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Folder not found or access denied"));
            }
        }

        // Save note and convert to response DTO
        Note savedNote = noteRepository.save(note);
        NoteResponse noteResponse = noteMapper.toNoteResponse(savedNote);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created successfully", noteResponse));
    }

    /**
     * Retrieves a specific note by ID
     * @param id The ID of the note to retrieve
     * @return The requested note if it belongs to the current user
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponse>> getNoteById(@PathVariable Long id) {
        try {
            Note note = noteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Security check: verify that the note belongs to the current user
            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            // Convert to response DTO
            NoteResponse noteResponse = noteMapper.toNoteResponse(note);

            return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", noteResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Updates an existing note
     * @param id The ID of the note to update
     * @param noteRequest The updated note details
     * @return The updated note as a response DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponse>> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest noteRequest) {
        try {
            Note note = noteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Security check
            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            // Update note fields from request
            noteMapper.updateNoteFromRequest(note, noteRequest);
            note.setUpdatedAt(LocalDateTime.now());

            // Update folder if provided
            if (noteRequest.getFolderId() != null) {
                Optional<Folder> folder = folderService.getFolderForCurrentUser(noteRequest.getFolderId());
                if (folder.isPresent()) {
                    note.setFolder(folder.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error("Folder not found or access denied"));
                }
            } else {
                // If folderId is null, remove from any folder
                note.setFolder(null);
            }

            // Save and convert to response DTO
            Note updatedNote = noteRepository.save(note);
            NoteResponse noteResponse = noteMapper.toNoteResponse(updatedNote);

            return ResponseEntity.ok(ApiResponse.success("Note updated successfully", noteResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Deletes a note by ID
     * @param id The ID of the note to delete
     * @return Success message if deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        try {
            Note note = noteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Security check
            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            noteRepository.delete(note);
            return ResponseEntity.ok(ApiResponse.success("Note deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all notes in a specific folder
     * @param folderId ID of the folder
     * @return List of notes in the specified folder
     */
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getNotesByFolder(@PathVariable Long folderId) {
        // Verify folder exists and belongs to current user
        Optional<Folder> folderOpt = folderService.getFolderForCurrentUser(folderId);

        if (folderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Folder not found or access denied"));
        }

        // Get notes by folder ID and user ID for additional security
        List<Note> notes = noteRepository.findByFolderIdAndUserId(folderId, userService.getCurrentUserId());

        // Convert to DTOs
        List<NoteResponse> noteResponses = notes.stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Notes retrieved successfully", noteResponses));
    }
}