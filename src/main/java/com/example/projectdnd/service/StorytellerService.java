package com.example.projectdnd.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class StorytellerService {

    private final ChatClient chatClient;

    public StorytellerService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

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