package com.example.projectdnd.service;

import com.example.projectdnd.model.dto.ExecutionResultDto;
import com.example.projectdnd.model.dto.RestResultDto;
import com.example.projectdnd.model.entity.User;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Realms;
import com.example.projectdnd.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GameService {

    private final UserRepository userRepository;
    private final StorytellerService storytellerService;
    private final Random random = new Random();

    public GameService(UserRepository userRepository, StorytellerService storytellerService) {
        this.userRepository = userRepository;
        this.storytellerService = storytellerService;
    }

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