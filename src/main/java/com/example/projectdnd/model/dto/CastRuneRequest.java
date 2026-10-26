package com.example.projectdnd.model.dto;

import com.example.projectdnd.model.enums.Realms;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastRuneRequest {
    private Realms rune;
    private int manaCost;
}