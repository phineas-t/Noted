package com.notesapp.notes_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Generate a secure key for HS512 algorithm (must be at least 512 bits / 64 bytes)
    private final SecretKey key = Jwts.SIG.HS512.key().build();
    private final long accessTokenExpiration = 1000 * 60 * 60;  // token expires in 1 hour

    public String generateToken(String username) {
        // Create a new JWT with the username as the subject
        // Set when it was issued and when it will expire (1 hour from now)
        // Sign it with the secret key
        return Jwts.builder()
                .subject(username)           // Sets the subject claim to the username
                .issuedAt(new Date())        // Sets the issued-at timestamp to now
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // Sets expiry time
                .signWith(key)               // Signs the JWT with our secret key
                .compact();                  // Builds the final compact JWT string
    }


    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            // Check if the token has expired
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // If parsing fails or there's any other exception, token is invalid
            return false;
        }
    }

    private Claims getClaims(String token) {
        // Parse the JWT, verify it with our key, and extract the claims
        return Jwts.parser()
                .verifyWith(key)             // Verify using the same secret key
                .build()
                .parseSignedClaims(token)    // Parse and validate the token
                .getPayload();               // Get the payload (claims)
    }

}

//JwtUtil handles token operations
//JwtRequestFilter handles request authentication
//SecurityConfig configures security rules
//CustomUserDetailsService loads user data