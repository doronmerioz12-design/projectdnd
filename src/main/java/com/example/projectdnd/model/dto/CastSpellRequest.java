package com.example.projectdnd.model.dto;

import com.example.projectdnd.model.enums.Magic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastSpellRequest {
    private Magic spell;
}