package com.example.projectdnd.model.enums;

import lombok.Getter;

@Getter
public enum Magic {
    FIREBALL(10),
    POISON_ARROW(10),
    ICE_WALL(12),
    TOPH_SIGHT(15),
    FLIGHT(20),
    LIGHTNING(15),
    LASER_EYE(25);

    private final int defaultManaCost;

    Magic(int defaultManaCost) {
        this.defaultManaCost = defaultManaCost;
    }

    Magic() {
        this.defaultManaCost = 0;
    }
}