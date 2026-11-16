package com.example.projectdnd.model.dto;

import com.example.projectdnd.model.enums.Realms;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the request payload for channeling a magical rune.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastRuneRequest {
    /** The magical realm rune to channel. */
    private Realms rune;
    /** The amount of mana invested into channeling the rune. */
    private int manaCost;
}