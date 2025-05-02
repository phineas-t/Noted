package com.notesapp.notes_app.service;

import com.notesapp.notes_app.model.User;
import com.notesapp.notes_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get the current authenticated user
     * @return the User object of the currently authenticated user
     * @throws RuntimeException if the user is not found
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    /**
     * Get the ID of the current authenticated user
     * @return the ID of the currently authenticated user
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Find a user by username
     *
     * @param username the username to search for
     * @return an Optional containing the User if found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}