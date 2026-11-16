package com.example.projectdnd.service;

import org.springframework.stereotype.Service;
import java.util.Random;

/**
 * Service providing polyhedral dice rolling utilities used in tabletop D&D mechanics.
 */
@Service
public class DiceService {

    private final Random random = new Random();


    /**
     * Rolls a standard 20-sided die (d20).
     *
     * @return a random integer between 1 and 20 (inclusive)
     */
    public int rollD20() {
        return random.nextInt(20) + 1;
    }


    /**
     * Rolls a custom polyhedral die with the specified number of sides.
     * When {@code sides} is 100, performs a percentile roll returning multiples of 10 (10 to 100).
     *
     * @param sides the number of faces on the die (e.g., 4, 6, 8, 10, 12, 20, 100)
     * @return a random roll result between 1 and {@code sides}, or a decade value (10..100) for d100
     */
    public int rollCustomDice(int sides) {

        if (sides == 100){
            return (random.nextInt(10) + 1) * 10;
        }
        return random.nextInt(sides) + 1;
    }
}