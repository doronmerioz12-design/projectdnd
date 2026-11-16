package com.example.projectdnd.controller;

import com.example.projectdnd.model.dto.CastRuneRequest;
import com.example.projectdnd.model.dto.CastSpellRequest;
import com.example.projectdnd.model.dto.ExecutionResultDto;
import com.example.projectdnd.model.dto.RestResultDto;
import com.example.projectdnd.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * REST controller for core Dungeons & Dragons gameplay actions.
 * Handles spell casting, rune channeling, and character resting for authenticated players.
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    /**
     * Constructs a {@code GameController} backed by the given {@link GameService}.
     *
     * @param gameService service executing gameplay mechanics and state updates
     */
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Casts a predefined spell for the currently authenticated user.
     *
     * @param principal the authenticated security principal representing the logged-in player
     * @param request   the request payload specifying which {@link com.example.projectdnd.model.enums.Magic} spell to cast
     * @return a {@link ResponseEntity} with the {@link ExecutionResultDto} containing roll results, mana changes, and AI story narration
     */
    @PostMapping("/cast-spell")
    public ResponseEntity<ExecutionResultDto> castSpell(
            Principal principal,
            @RequestBody CastSpellRequest request) {

        // Extract authenticated username directly from the verified JWT
        String username = principal.getName();

        ExecutionResultDto result = gameService.castAbility(
                username,
                request.getSpell()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * Channels a magical rune with a custom mana investment for the currently authenticated user.
     *
     * @param principal the authenticated security principal representing the logged-in player
     * @param request   the request payload specifying the {@link com.example.projectdnd.model.enums.Realms} rune and mana cost
     * @return a {@link ResponseEntity} with the {@link ExecutionResultDto} containing roll results, mana changes, and AI story narration
     */
    @PostMapping("/cast-rune")
    public ResponseEntity<ExecutionResultDto> castRune(
            Principal principal,
            @RequestBody CastRuneRequest request) {

        // Extract authenticated username directly from the verified JWT
        String username = principal.getName();

        ExecutionResultDto result = gameService.castAbility(
                username,
                request.getRune(),
                request.getManaCost()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * Performs a long rest for the authenticated user's character, restoring HP and mana to maximum.
     *
     * @param principal the authenticated security principal representing the logged-in player
     * @return a {@link ResponseEntity} with the {@link RestResultDto} detailing restored stats and AI rest narration
     */
    @PostMapping("/rest")
    public ResponseEntity<RestResultDto> rest(Principal principal) {
        String username = principal.getName();
        RestResultDto result = gameService.takeRest(username);
        return ResponseEntity.ok(result);
    }
}