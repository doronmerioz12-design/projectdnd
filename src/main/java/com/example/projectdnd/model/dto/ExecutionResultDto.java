package com.example.projectdnd.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutionResultDto {
    private boolean success;
    private String message;
    private int manaSpent;
    private int remainingMana;
    private int diceRollResult;
    private String narrativeStory;
}