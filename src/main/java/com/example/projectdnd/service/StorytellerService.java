package com.example.projectdnd.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI-powered Dungeon Master (Storyteller) service that uses Spring AI and Google GenAI (Gemini)
 * to generate dynamic, atmospheric fantasy narrations for player actions and rests.
 * Includes graceful fallback descriptions if the external AI service is unavailable.
 */
@Service
public class StorytellerService {

    private final ChatClient chatClient;

    /**
     * Constructs a {@code StorytellerService} using the provided Spring AI {@link ChatClient.Builder}.
     *
     * @param chatClientBuilder builder used to create the underlying {@link ChatClient}
     */
    public StorytellerService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Generates a 2-3 sentence fantasy narration for a combat or magic action based on the
     * character's identity, the attempted ability, the d20 roll, and the Difficulty Class (DC).
     * Special prompts are applied for critical successes (Natural 20) and critical failures (Natural 1).
     *
     * @param characterName  the name of the player's character
     * @param characterClass the class/vocation of the character
     * @param actionName     the name of the spell or rune attempted
     * @param diceRoll       the d20 roll result
     * @param dc             the target Difficulty Class (DC)
     * @param isSuccess      whether the action succeeded mechanically
     * @return the AI-generated narrative string, or a fallback description if the AI call fails
     */
    public String narrateAction(
            String characterName,
            String characterClass,
            String actionName,
            int diceRoll,
            int dc,
            boolean isSuccess
    ) {
        String outcome = isSuccess ? "SUCCESS" : "FAILURE";
        if (diceRoll == 20) {
            outcome = "CRITICAL SUCCESS (Natural 20!)";
        } else if (diceRoll == 1) {
            outcome = "CRITICAL FAILURE (Natural 1!)";
        }

        String prompt = String.format("""
            You are a Dungeons & Dragons Dungeon Master (Storyteller).
            Narrate what happens in 2-3 atmospheric, vivid sentences in English.
            
            Character: %s the %s
            Action attempted: %s
            Dice roll: %d against Difficulty Class (DC): %d
            Outcome: %s
            
            Guidelines:
            - If Natural 20: Make the outcome epic, legendary, and spectacular!
            - If Natural 1: Describe a chaotic, humorous, or disastrous backfire!
            - Otherwise: Provide an engaging fantasy narration matching the success or failure.
            """, characterName, characterClass, actionName, diceRoll, dc, outcome);

        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            // Print the exact error to see why Gemini failed
            System.err.println(">>> Storyteller AI Error: " + e.getMessage());
            e.printStackTrace();

            return isSuccess
                    ? "The arcane energies align perfectly as the magic surges forth into the target!"
                    : "The arcane power abruptly unravels into thin air, leaving behind only faint wisps of smoke.";
        }
    }

    /**
     * Generates a calming 2-3 sentence fantasy narration describing the character taking
     * a long rest by a campfire to recover Hit Points and Mana.
     *
     * @param characterName  the name of the player's character
     * @param characterClass the class/vocation of the character
     * @param hpRestored     the amount of Hit Points recovered
     * @param manaRestored   the amount of Mana recovered
     * @return the AI-generated rest narrative, or a fallback campfire description if the AI call fails
     */
    public String narrateRest(String characterName, String characterClass, int hpRestored, int manaRestored) {
        String prompt = String.format("""
        You are a Dungeons & Dragons Dungeon Master (Storyteller).
        Narrate a short, atmospheric rest scene in 2-3 sentences in English.
        
        Character: %s the %s
        Action: Taking a long rest by a campfire to recover strength.
        Recovered: %d Hit Points and %d Mana.
        
        Guidelines:
        - Describe how the character recuperates, meditates, or tends to their wounds.
        - Create a calming, immersive fantasy atmosphere.
        """, characterName, characterClass, hpRestored, manaRestored);

        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            return characterName + " settles by the warm embers of the campfire, slowly regaining vitality and arcane focus.";
        }
    }
}