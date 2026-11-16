package com.example.projectdnd.model.dto;

import com.example.projectdnd.model.enums.Magic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the request payload for casting a predefined spell.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastSpellRequest {
    /** The spell to be cast by the character. */
    private Magic spell;
}