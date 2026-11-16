package com.example.projectdnd.model.entity;

import com.example.projectdnd.model.enums.CharacterClass;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Race;
import com.example.projectdnd.model.enums.Realms;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a registered player account and their associated D&D character
 * stored in the {@code users} table.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Unique primary key identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Account credentials and security
    /** Unique login username for authentication. */
    @Column(unique = true, nullable = false)
    private String username;

    /** BCrypt-hashed password for authentication. */
    @Column(nullable = false)
    private String password;

    /** Security role assigned to the user (e.g., "USER", "ROLE_PLAYER"). */
    private String role;

    // Character identity
    /** In-game display name of the player's character. */
    private String characterName;

    /** Fantasy race of the character. */
    @Enumerated(EnumType.STRING)
    private Race race;

    /** Combat and magic class of the character. */
    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;

    // Progression and stats
    /** Current experience level of the character. */
    private int level;
    /** Current Hit Points (health) of the character. */
    private int currentHp;
    /** Maximum Hit Points capacity of the character. */
    private int maxHp;
    /** Current Mana points available for casting spells and runes. */
    private int currentMana;
    /** Maximum Mana capacity of the character. */
    private int maxMana;

    // Attribute scores
    /** Intelligence attribute score governing arcane proficiency. */
    private int intelligence;
    /** Strength attribute score governing physical power. */
    private int strength;
    /** Dexterity attribute score governing agility and reflexes. */
    private int dexterity;

    // Known runes mapping table
    /** Set of elemental/metaphysical runes unlocked by the character. */
    @ElementCollection(targetClass = Realms.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_realms", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "realm")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Realms> knownRealms = new HashSet<>();

    /**
     * Checks whether the character has learned a specific realm rune.
     *
     * @param rune the {@link Realms} rune to check
     * @return {@code true} if the rune is in the character's known realms, {@code false} otherwise
     */
    public boolean hasRune(Realms rune) {
        return knownRealms != null && knownRealms.contains(rune);
    }

    // Known spells mapping table
    /** Set of predefined spells unlocked by the character. */
    @ElementCollection(targetClass = Magic.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_magic", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "magic")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Magic> knownMagic = new HashSet<>();

    /**
     * Checks whether the character has learned a specific spell.
     *
     * @param magic the {@link Magic} spell to check
     * @return {@code true} if the spell is in the character's known magic set, {@code false} otherwise
     */
    public boolean hasMagic(Magic magic) {
        return knownMagic != null && knownMagic.contains(magic);
    }
}