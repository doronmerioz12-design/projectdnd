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

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

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

    @PostMapping("/rest")
    public ResponseEntity<RestResultDto> rest(Principal principal) {
        String username = principal.getName();
        RestResultDto result = gameService.takeRest(username);
        return ResponseEntity.ok(result);
    }
}