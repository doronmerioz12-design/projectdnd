package com.example.projectdnd.model.entity;

import com.example.projectdnd.model.enums.AbilityType;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Realms;
import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing a magical spell or rune ability stored in the {@code spells} table.
 * Defines the realm, magic classification, mana cost, difficulty class (DC), and visual styling.
 */
@Entity
@Table(name = "spells")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spell {

    /** Unique primary key identifier for the spell. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique display name of the spell or ability. */
    @Column(nullable = false, unique = true)
    private String name;

    /** Detailed lore or mechanical description of the spell (up to 500 characters). */
    @Column(length = 500)
    private String description;

    /** Elemental or metaphysical realm associated with this ability. */
    @Enumerated(EnumType.STRING)
    private Realms realm;

    /** Specific predefined magic type, if applicable. */
    @Enumerated(EnumType.STRING)
    private Magic magicType;

    /** Category distinguishing fixed spells from flexible runes. */
    @Enumerated(EnumType.STRING)
    private AbilityType abilityType;

    /** Indicates whether this ability acts as a dynamic rune with variable mana scaling. */
    private boolean isDynamicRune;

    /** Base mana cost required to cast the spell. */
    private int manaCost;
    /** Base Difficulty Class (DC) target required on a dice roll for a successful cast. */
    private int baseDc;
    /** Hexadecimal color code associated with the spell for UI representation. */
    private String colorHex;
}