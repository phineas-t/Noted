package com.notesapp.notes_app.controller;

import com.notesapp.notes_app.dto.ApiResponse;
import com.notesapp.notes_app.model.Note;
import com.notesapp.notes_app.model.User;
import com.notesapp.notes_app.repository.NoteRepository;
import com.notesapp.notes_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for handling Note related operations
 */
@RestController // Marks this class as a controller where every method returns a domain object instead of a view
@RequestMapping("/api/notes") // Base URL mapping for all endpoints in this controller
public class NoteController {

    @Autowired // Injects the NoteRepository dependency
    private NoteRepository noteRepository;

    @Autowired // Injects the UserService dependency
    private UserService userService;

    /**
     * Retrieves all notes belonging to the currently authenticated user
     * @return List of notes for the current user
     */
    @GetMapping // Maps HTTP GET requests to this method at the base URL
    public ResponseEntity<ApiResponse<List<Note>>> getNotes() {
        // Get current user ID from the UserService
        List<Note> notes = noteRepository.findByUserId(userService.getCurrentUserId());

        // Return notes with HTTP 200 OK status and success message
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved successfully", notes));
    }

    /**
     * Creates a new note for the currently authenticated user
     * @param note The note object from request body
     * @return The created note with ID
     */
    @PostMapping // Maps HTTP POST requests to this method at the base URL
    public ResponseEntity<ApiResponse<Note>> createNote(@RequestBody Note note) {
        // Get the complete user object of currently authenticated user
        User currentUser = userService.getCurrentUser();

        // Associate the note with the current user
        note.setUser(currentUser);

        // Set creation and update timestamps to current time
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        // Save the note to database
        Note savedNote = noteRepository.save(note);

        // Return the saved note with HTTP 201 CREATED status
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created successfully", savedNote));
    }

    /**
     * Retrieves a specific note by ID
     * @param id The ID of the note to retrieve
     * @return The requested note if it belongs to the current user
     */
    @GetMapping("/{id}") // Maps HTTP GET requests for a specific note ID
    public ResponseEntity<ApiResponse<Note>> getNoteById(@PathVariable Long id) {
        try {
            // Find the note by ID or throw exception if not found
            Note note = noteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Security check: verify that the note belongs to the current user
            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
                // Return HTTP 403 FORBIDDEN if note doesn't belong to current user
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            // Return the note with HTTP 200 OK status
            return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", note));
        } catch (RuntimeException e) {
            // Handle not found exception with HTTP 404 NOT FOUND status
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Updates an existing note
     * @param id The ID of the note to update
     * @param noteDetails The updated note details
     * @return The updated note
     */
    @PutMapping("/{id}") // Maps HTTP PUT requests for a specific note ID
    public ResponseEntity<ApiResponse<Note>> updateNote(@PathVariable Long id, @RequestBody Note noteDetails) {
        try {
            // Find the note by ID or throw exception if not found
            Note note = noteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Security check: verify that the note belongs to the current user
            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
                // Return HTTP 403 FORBIDDEN if note doesn't belong to current user
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            // Update the note fields with new values
            note.setTitle(noteDetails.getTitle());
            note.setContent(noteDetails.getContent());
            note.setUpdatedAt(LocalDateTime.now()); // Update the modification timestamp

            // Save the updated note to database
            Note updatedNote = noteRepository.save(note);

            // Return the updated note with HTTP 200 OK status
            return ResponseEntity.ok(ApiResponse.success("Note updated successfully", updatedNote));
        } catch (RuntimeException e) {
            // Handle not found exception with HTTP 404 NOT FOUND status
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Deletes a note by ID
     * @param id The ID of the note to delete
     * @return Success message if deleted successfully
     */
    @DeleteMapping("/{id}") // Maps HTTP DELETE requests for a specific note ID
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        try {
            // Find the note by ID or throw exception if not found
            Note note = noteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Security check: verify that the note belongs to the current user
            if (!note.getUser().getId().equals(userService.getCurrentUserId())) {
                // Return HTTP 403 FORBIDDEN if note doesn't belong to current user
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied"));
            }

            // Delete the note from database
            noteRepository.delete(note);

            // Return success message with HTTP 200 OK status
            return ResponseEntity.ok(ApiResponse.success("Note deleted successfully", null));
        } catch (RuntimeException e) {
            // Handle not found exception with HTTP 404 NOT FOUND status
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}