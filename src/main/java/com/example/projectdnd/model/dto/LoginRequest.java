package com.example.projectdnd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) carrying user credentials for authentication.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    /** The player's unique account username. */
    private String username;
    /** The player's plaintext password to be verified against the stored BCrypt hash. */
    private String password;
}