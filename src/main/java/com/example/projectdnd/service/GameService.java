package com.example.projectdnd.service;

import com.example.projectdnd.model.dto.ExecutionResultDto;
import com.example.projectdnd.model.dto.RestResultDto;
import com.example.projectdnd.model.entity.User;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Realms;
import com.example.projectdnd.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Core gameplay service handling character actions such as casting spells,
 * channeling runes, and taking long rests. Coordinates persistence updates
 * with {@link UserRepository} and narrative generation with {@link StorytellerService}.
 */
@Service
public class GameService {

    private final UserRepository userRepository;
    private final StorytellerService storytellerService;
    private final Random random = new Random();

    /**
     * Constructs a {@code GameService} with the required repository and AI storyteller dependencies.
     *
     * @param userRepository     repository for querying and updating player character state
     * @param storytellerService AI service for generating immersive D&D narrative descriptions
     */
    public GameService(UserRepository userRepository, StorytellerService storytellerService) {
        this.userRepository = userRepository;
        this.storytellerService = storytellerService;
    }

    /**
     * Executes a predefined spell cast for the specified user.
     * Verifies sufficient mana (fixed cost of 10), deducts mana, rolls a d20 against DC 10,
     * persists the updated character state, and requests an AI narrative description.
     *
     * @param username the username of the player casting the spell
     * @param spell    the {@link Magic} spell being cast
     * @return an {@link ExecutionResultDto} containing roll outcome, mana usage, and story narration
     * @throws RuntimeException if the user is not found or does not have enough mana
     */
    public ExecutionResultDto castAbility(String username, Magic spell) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        int manaCost = 10;
        int dc = 10;

        if (user.getCurrentMana() < manaCost) {
            throw new RuntimeException("Not enough mana!");
        }

        user.setCurrentMana(user.getCurrentMana() - manaCost);
        userRepository.save(user);

        int roll = random.nextInt(20) + 1;
        boolean isSuccess = roll >= dc;

        String message = isSuccess
                ? "Spell " + spell.name() + " cast successfully! Roll: " +
                roll + " (beat DC " + dc + ")"
                : "Spell fizzled! Roll of " + roll + " failed to meet DC " + dc;


        String story = storytellerService.narrateAction(
                user.getCharacterName(),
                user.getCharacterClass() !=
                        null ? user.getCharacterClass().name() : "Adventurer",
                spell.name(),
                roll,
                dc,
                isSuccess
        );


        return ExecutionResultDto.builder()
                .success(isSuccess)
                .message(message)
                .manaSpent(manaCost)
                .remainingMana(user.getCurrentMana())
                .diceRollResult(roll)
                .narrativeStory(story)
                .build();
    }

    /**
     * Channels a magical realm rune with a custom mana cost for the specified user.
     * Verifies sufficient mana, deducts the specified mana cost, rolls a d20 plus the character's
     * level against DC 12, persists the updated user, and generates AI narration.
     *
     * @param username the username of the player channeling the rune
     * @param rune     the {@link Realms} rune being channeled
     * @param manaCost the amount of mana to spend on channeling the rune
     * @return an {@link ExecutionResultDto} containing roll outcome, mana usage, and story narration
     * @throws RuntimeException if the user is not found or does not have enough mana
     */
    public ExecutionResultDto castAbility(String username, Realms rune, int manaCost) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        int dc = 12;

        if (user.getCurrentMana() < manaCost) {
            throw new RuntimeException("Not enough mana!");
        }

        user.setCurrentMana(user.getCurrentMana() - manaCost);
        userRepository.save(user);

        int baseRoll = random.nextInt(20) + 1;
        int totalRoll = baseRoll + user.getLevel();
        boolean isSuccess = totalRoll >= dc;

        String message = isSuccess
                ? "Rune " + rune.name() + " channeled successfully! Roll: " +
                baseRoll + " + Level: " + user.getLevel() + " = " + totalRoll
                : "Rune " + rune.name() + " backfired or fizzled! Roll: " +
                baseRoll + " + Level: " + user.getLevel() + " = " + totalRoll +
                " (Failed DC " + dc + ")";


        String story = storytellerService.narrateAction(
                user.getCharacterName(),
                user.getCharacterClass() !=
                        null ? user.getCharacterClass().name() : "Adventurer",
                "Rune: " + rune.name(),
                baseRoll,
                dc,
                isSuccess
        );

        return ExecutionResultDto.builder()
                .success(isSuccess)
                .message(message)
                .manaSpent(manaCost)
                .remainingMana(user.getCurrentMana())
                .diceRollResult(baseRoll)
                .narrativeStory(story)
                .build();
    }

    /**
     * Executes a long rest for the specified user, restoring their Hit Points and Mana
     * to their respective maximum values and generating a campfire rest narration.
     *
     * @param username the username of the player taking a rest
     * @return a {@link RestResultDto} detailing the restored HP/Mana, current totals, and AI story
     * @throws RuntimeException if the user is not found or if saving fails
     */
    public RestResultDto takeRest(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        int hpRestored = user.getMaxHp() - user.getCurrentHp();
        int manaRestored = user.getMaxMana() - user.getCurrentMana();

        user.setCurrentHp(user.getMaxHp());
        user.setCurrentMana(user.getMaxMana());
        userRepository.save(user);

        String characterClass = user.getCharacterClass() != null ? user.getCharacterClass().name() : "Adventurer";
        String story = storytellerService.narrateRest(user.getCharacterName(), characterClass, hpRestored, manaRestored);

        return RestResultDto.builder()
                .message("Long rest completed successfully! Health and mana fully restored.")
                .restoredHp(hpRestored)
                .restoredMana(manaRestored)
                .currentHp(user.getCurrentHp())
                .currentMana(user.getCurrentMana())
                .narrativeStory(story)
                .build();
    }
}