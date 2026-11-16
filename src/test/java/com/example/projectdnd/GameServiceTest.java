package com.example.projectdnd;

import com.example.projectdnd.model.dto.ExecutionResultDto;
import com.example.projectdnd.model.dto.RestResultDto;
import com.example.projectdnd.model.entity.User;
import com.example.projectdnd.model.enums.CharacterClass;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.model.enums.Race;
import com.example.projectdnd.model.enums.Realms;
import com.example.projectdnd.repository.UserRepository;
import com.example.projectdnd.service.GameService;
import com.example.projectdnd.service.StorytellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GameService} using Mockito to isolate business logic from
 * database persistence ({@link UserRepository}) and external AI calls ({@link StorytellerService}).
 */
@ExtendWith({MockitoExtension.class, CustomReportWatcher.class})
public class GameServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    StorytellerService storytellerService;

    @InjectMocks
    GameService gameService;

    private User character;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    /**
     * Initializes a test character ("Frodo") with baseline stats before each unit test.
     */
    @BeforeEach
    void setup() {
        character = User.builder()
                .username("Frodo")
                .password("SecretPass123")
                .role("ROLE_PLAYER")
                .characterName("Frodo")
                .race(Race.MAN)
                .characterClass(CharacterClass.WIZARD)
                .level(5)
                .currentHp(45)
                .maxHp(50)
                .currentMana(50)
                .maxMana(50)
                .intelligence(12)
                .strength(18)
                .dexterity(15)
                .knownRealms(new HashSet<>(Set.of(Realms.FIRE)))
                .build();
    }

    /**
     * Verifies that casting a spell with sufficient mana deducts the expected mana cost,
     * saves the updated character in the repository, and returns the AI-narrated execution result.
     */
    @Test
    @DisplayName("Test Magic Cast Successful")
    void testMagicCastSuccessful() {
        when(userRepository.findByUsername("Frodo")).thenReturn(Optional.of(character));
        when(storytellerService.narrateAction(
                anyString(), anyString(), anyString(), anyInt(), anyInt(), anyBoolean()
        )).thenReturn("Epic fireball narration!");

        ExecutionResultDto result = gameService.castAbility("Frodo", Magic.FIREBALL);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        CustomReportWatcher.recordDetails(
                "User: Frodo (Mana: 50), Spell: FIREBALL",
                "Saved Mana: 40, Spent: 10, Story: 'Epic fireball narration!'",
                "Saved Mana: " + savedUser.getCurrentMana() + ", Spent: " + result.getManaSpent() + ", Story: '" + result.getNarrativeStory() + "'"
        );

        assertAll("Verify spell cast outcome",
                () -> assertEquals(40, savedUser.getCurrentMana()),
                () -> assertEquals(10, result.getManaSpent()),
                () -> assertEquals(40, result.getRemainingMana()),
                () -> assertEquals("Epic fireball narration!", result.getNarrativeStory())
        );
    }

    /**
     * Verifies that attempting to cast a spell without enough mana throws a {@link RuntimeException}
     * ("Not enough mana!") and avoids saving to the repository or invoking the AI storyteller.
     */
    @Test
    @DisplayName("Test Not Enough Mana")
    void testMagicCastFailed() {
        character.setCurrentMana(5);
        when(userRepository.findByUsername("Frodo")).thenReturn(Optional.of(character));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gameService.castAbility("Frodo", Magic.FIREBALL);
        });

        CustomReportWatcher.recordDetails(
                "User: Frodo (Mana: 5), Spell: FIREBALL",
                "RuntimeException: 'Not enough mana!'",
                exception.getClass().getSimpleName() + ": '" + exception.getMessage() + "'"
        );

        assertEquals("Not enough mana!", exception.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(storytellerService);
    }

    /**
     * Verifies that taking a long rest accurately calculates restored HP and Mana,
     * resets both stats to their maximum capacities, and persists the character.
     */
    @Test
    @DisplayName("Rest Test")
    void testRest() {
        character.setCurrentHp(10);
        character.setCurrentMana(15);
        character.setMaxHp(25);
        character.setMaxMana(50);
        when(userRepository.findByUsername("Frodo")).thenReturn(Optional.of(character));

        RestResultDto result = gameService.takeRest("Frodo");

        CustomReportWatcher.recordDetails(
                "User: Frodo (HP: 10/25, Mana: 15/50)",
                "Restored HP: 15, Restored Mana: 35 (Full: 25/50)",
                "Restored HP: " + result.getRestoredHp() + ", Restored Mana: " + result.getRestoredMana() + " (Full: " + result.getCurrentHp() + "/" + result.getCurrentMana() + ")"
        );

        assertAll(
                () -> assertEquals(35, result.getRestoredMana()),
                () -> assertEquals(15, result.getRestoredHp()),
                () -> assertEquals(25, result.getCurrentHp()),
                () -> assertEquals(50, result.getCurrentMana())
        );

        verify(userRepository).save(character);
    }

    /**
     * Verifies that if a database failure occurs when saving the user during a rest,
     * the exception is propagated and the AI storyteller service is not called.
     */
    @Test
    @DisplayName("DB Error Test")
    void DBErrorTest() {
        when(userRepository.findByUsername("Frodo")).thenReturn(Optional.of(character));
        when(userRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gameService.takeRest("Frodo");
        });

        CustomReportWatcher.recordDetails(
                "User: Frodo, Action: takeRest (DB save fails)",
                "RuntimeException: 'DB Error'",
                exception.getClass().getSimpleName() + ": '" + exception.getMessage() + "'"
        );

        assertEquals("DB Error", exception.getMessage());
        verifyNoInteractions(storytellerService);
    }
}