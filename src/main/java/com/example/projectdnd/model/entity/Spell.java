package com.example.projectdnd.model.entity;

import com.example.projectdnd.model.enums.AbilityType;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Realms;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spells")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private Realms realm;

    @Enumerated(EnumType.STRING)
    private Magic magicType;

    @Enumerated(EnumType.STRING)
    private AbilityType abilityType;

    private boolean isDynamicRune;

    private int manaCost;
    private int baseDc;
    private String colorHex;
}