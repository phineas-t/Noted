package com.notesapp.notes_app.controller;

import com.notesapp.notes_app.dto.ApiResponse;
import com.notesapp.notes_app.dto.JwtResponse;
import com.notesapp.notes_app.dto.RefreshTokenRequest;
import com.notesapp.notes_app.model.RefreshToken;
import com.notesapp.notes_app.model.User;
import com.notesapp.notes_app.repository.UserRepository;
import com.notesapp.notes_app.security.JwtUtil;
import com.notesapp.notes_app.service.RefreshTokenService;
import com.notesapp.notes_app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for handling authentication operations
 */
@RestController // Marks this class as a controller where every method returns a domain object instead of a view
@RequestMapping("/api/auth") // Base URL mapping for all endpoints in this controller
public class AuthController {

    @Autowired // Injects the AuthenticationManager dependency
    private AuthenticationManager authenticationManager;

    @Autowired // Injects the JwtUtil dependency for token operations
    private JwtUtil jwtUtil;

    @Autowired // Injects the UserRepository dependency for database operations
    private UserRepository userRepository;

    @Autowired // Injects the BCryptPasswordEncoder for password hashing
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired // Injects the RefreshTokenService for refresh token operations
    private RefreshTokenService refreshTokenService;

    @Autowired // Injects the UserService for user-related operations
    private UserService userService;

    /**
     * Registers a new user
     * @param user The user object from request body containing registration details
     * @return Success message if registration successful
     */
    @PostMapping("/register") // Maps HTTP POST requests to this method at /api/auth/register
    public ResponseEntity<ApiResponse<?>> register(@RequestBody User user) {
        // Check if the username is already taken
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // Return error response with HTTP 400 BAD REQUEST if username exists
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Username already exists"));
        }

        // Hash the user's password for security
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save the new user to database
        User savedUser = userRepository.save(user);

        // Create a response map with user details
        Map<String, String> response = new HashMap<>();
        response.put("username", savedUser.getUsername());

        // Return success response with HTTP 200 OK
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Authenticates a user and returns JWT tokens
     * @param loginRequest The user credentials from request body
     * @return JWT access and refresh tokens if authentication successful
     */
    @PostMapping("/login") // Maps HTTP POST requests to this method at /api/auth/login
    public ResponseEntity<ApiResponse<?>> login(@RequestBody User loginRequest) {
        try {
            // Attempt to authenticate the user with provided credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Store authentication object in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String username = loginRequest.getUsername();

            // Generate a new JWT access token
            String accessToken = jwtUtil.generateToken(username);

            // Create a new refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

            // Create JWT response object with tokens
            JwtResponse jwtResponse = new JwtResponse(
                    accessToken,
                    refreshToken.getToken(),
                    "Bearer", // Token type
                    username
            );

            // Return tokens with HTTP 200 OK
            return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
        } catch (BadCredentialsException e) {
            // Handle invalid credentials with HTTP 401 UNAUTHORIZED
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password"));
        } catch (Exception e) {
            // Handle other exceptions with HTTP 500 INTERNAL SERVER ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Authentication failed: " + e.getMessage()));
        }
    }

    // Maps HTTP POST requests to this method at /api/auth/refresh-token
    /**
     * Issues a new access token using a valid refresh token
     * @param request The refresh token request object
     * @return New JWT access token if refresh token is valid
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody RefreshTokenRequest request) {
        // Extract refresh token from request
        String requestRefreshToken = request.getRefreshToken();

        Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(requestRefreshToken);

        if (tokenOptional.isEmpty()) {
            // Handle case where refresh token is not found
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Refresh token not found"));
        }

        RefreshToken refreshToken = tokenOptional.get();

        // Check if token has been revoked
        if (refreshToken.isRevoked()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Refresh token was revoked"));
        }

        // Check if token has expired
        if (!refreshTokenService.verifyExpiration(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Refresh token was expired"));
        }

        // Get user associated with the token
        User user = refreshToken.getUser();

        // Generate new access token
        String accessToken = jwtUtil.generateToken(user.getUsername());

        // Create JWT response with new access token and existing refresh token
        JwtResponse jwtResponse = new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer", // Token type
                user.getUsername()
        );

        // Return new tokens with HTTP 200 OK
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", jwtResponse));
    }
    /**
     * Logs out a user by revoking all their refresh tokens
     * @return Success message if logout successful
     */
    @PostMapping("/logout") // Maps HTTP POST requests to this method at /api/auth/logout
    public ResponseEntity<ApiResponse<?>> logout() {
        // Get the currently authenticated user
        User currentUser = userService.getCurrentUser();
        // Revoke all refresh tokens for this user
        refreshTokenService.revokeAllUserTokens(currentUser);

        // Return success message with HTTP 200 OK
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}