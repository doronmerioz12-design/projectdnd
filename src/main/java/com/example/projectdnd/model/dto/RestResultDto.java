package com.example.projectdnd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestResultDto {
    private String message;
    private int restoredHp;
    private int restoredMana;
    private int currentHp;
    private int currentMana;
    private String narrativeStory;
}