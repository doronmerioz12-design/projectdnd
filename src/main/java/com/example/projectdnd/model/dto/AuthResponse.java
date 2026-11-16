package com.example.projectdnd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the authentication response sent to the client
 * upon a successful login.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    /** Signed JWT bearer token used to authenticate subsequent API requests. */
    private String token;
}