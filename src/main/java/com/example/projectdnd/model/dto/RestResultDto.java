package com.example.projectdnd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the outcome of a character's long rest,
 * detailing restored hit points, restored mana, and AI-generated rest narration.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestResultDto {
    /** Status message confirming completion of the rest. */
    private String message;
    /** Amount of Hit Points (HP) recovered during the rest. */
    private int restoredHp;
    /** Amount of Mana recovered during the rest. */
    private int restoredMana;
    /** Character's current Hit Points after resting. */
    private int currentHp;
    /** Character's current Mana after resting. */
    private int currentMana;
    /** Atmospheric campfire rest scene narrated by the AI Dungeon Master. */
    private String narrativeStory;
}