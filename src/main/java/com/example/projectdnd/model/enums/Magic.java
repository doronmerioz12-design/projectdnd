package com.example.projectdnd.model.enums;

import lombok.Getter;

/**
 * Enumeration of predefined spells available to characters, each associated with a default mana cost.
 */
@Getter
public enum Magic {
    /** Launches an explosive orb of fire at the target. */
    FIREBALL(10),
    /** Fires a conjured arrow coated in virulent toxin. */
    POISON_ARROW(10),
    /** Raises a protective barrier of solid glacial ice. */
    ICE_WALL(12),
    /** Grants seismic tremorsense to perceive surroundings through earth vibrations. */
    TOPH_SIGHT(15),
    /** Bestows the ability to levitate and fly through the air. */
    FLIGHT(20),
    /** Calls down a crackling bolt of electrical energy. */
    LIGHTNING(15),
    /** Emits a concentrated beam of searing arcane energy from the caster's eyes. */
    LASER_EYE(25);

    /** Default mana points required to cast this spell. */
    private final int defaultManaCost;

    /**
     * Constructs a magic spell enum constant with a specified default mana cost.
     *
     * @param defaultManaCost the mana cost required to cast the spell
     */
    Magic(int defaultManaCost) {
        this.defaultManaCost = defaultManaCost;
    }

    /**
     * Constructs a magic spell enum constant with a default mana cost of zero.
     */
    Magic() {
        this.defaultManaCost = 0;
    }
}