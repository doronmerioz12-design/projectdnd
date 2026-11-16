package com.example.projectdnd;

import com.example.projectdnd.service.DiceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DiceService}, verifying boundary conditions and distribution validity
 * for standard d20 rolls and custom polyhedral dice (d4, d6, d8, d10, d12, d20, d100).
 */
@ExtendWith(CustomReportWatcher.class)
public class DiceServiceTest {

    private final DiceService diceService;

    /**
     * Initializes a fresh instance of {@link DiceService} for testing.
     */
    public DiceServiceTest() {
        diceService = new DiceService();
    }

    /**
     * Repeatedly rolls a 20-sided die (d20) 50 times to ensure every outcome stays
     * strictly within the valid range of 1 to 20.
     */
    @DisplayName("50 time d20 test")
    @RepeatedTest(value = 50)
    public void testDiceService() {
        int dice = diceService.rollD20();

        CustomReportWatcher.recordDetails(
                "d20 roll",
                "Between 1 and 20",
                "Rolled: " + dice
        );

        assertTrue(dice >= 1 && dice <= 20);
    }

    /**
     * Parameterized test that verifies custom dice rolls across standard polyhedral sizes
     * (4, 6, 8, 10, 12, 20, and 100), confirming normal bounds (1..sides) and decade multiples
     * (10..100) for percentile d100 rolls.
     *
     * @param sides the number of sides on the die being tested
     */
    @DisplayName("Custom dice test")
    @ParameterizedTest(name = "Roll d{0}")
    @ValueSource(ints = {4, 6, 8, 10, 12, 20, 100})
    public void testRollCustom(int sides) {
        int result = diceService.rollCustomDice(sides);

        if (sides == 100) {
            CustomReportWatcher.recordDetails(
                    "d100 percentile roll",
                    "Multiples of 10 between 10 and 100",
                    "Rolled: " + result
            );
            assertTrue(result >= 10 && result <= 100 && result % 10 == 0);
        } else {
            CustomReportWatcher.recordDetails(
                    "d" + sides + " roll",
                    "Between 1 and " + sides,
                    "Rolled: " + result
            );
            assertTrue(result >= 1 && result <= sides);
        }
    }
}