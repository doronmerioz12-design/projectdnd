package com.example.projectdnd.model.entity;

import com.example.projectdnd.model.enums.CharacterClass;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Race;
import com.example.projectdnd.model.enums.Realms;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Account credentials and security
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;

    // Character identity
    private String characterName;

    @Enumerated(EnumType.STRING)
    private Race race;

    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;

    // Progression and stats
    private int level;
    private int currentHp;
    private int maxHp;
    private int currentMana;
    private int maxMana;

    // Attribute scores
    private int intelligence;
    private int strength;
    private int dexterity;

    // Known runes mapping table
    @ElementCollection(targetClass = Realms.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_realms", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "realm")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Realms> knownRealms = new HashSet<>();

    public boolean hasRune(Realms rune) {
        return knownRealms != null && knownRealms.contains(rune);
    }

    // Known spells mapping table
    @ElementCollection(targetClass = Magic.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_magic", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "magic")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Magic> knownMagic = new HashSet<>();

    public boolean hasMagic(Magic magic) {
        return knownMagic != null && knownMagic.contains(magic);
    }
}