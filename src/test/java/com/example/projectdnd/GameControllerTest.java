package com.example.projectdnd;

import com.example.projectdnd.controller.GameController;
import com.example.projectdnd.model.dto.ExecutionResultDto;
import com.example.projectdnd.model.enums.Magic;
import com.example.projectdnd.security.JwtService;
import com.example.projectdnd.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer slice tests ({@link WebMvcTest}) for {@link GameController}.
 * Uses {@link MockMvc} and mocked service beans to verify HTTP endpoints, JSON serialization,
 * global exception handling (400 Bad Request), and Spring Security authentication guards.
 */
@ExtendWith(CustomReportWatcher.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    /**
     * Verifies that an authenticated user casting a valid spell via {@code POST /api/game/cast-spell}
     * receives an HTTP 200 OK response containing the expected execution result and narrative story.
     *
     * @throws Exception if the MockMvc request fails
     */
    @Test
    @DisplayName("Cast Test Success")
    @WithMockUser(username = "Frodo")
    void castTestSuccess() throws Exception {
        ExecutionResultDto mockResult = ExecutionResultDto.builder()
                .success(true)
                .message("Spell FIREBALL cast successfully!")
                .manaSpent(10)
                .remainingMana(40)
                .diceRollResult(15)
                .narrativeStory("Boom!")
                .build();

        when(gameService.castAbility("Frodo", Magic.FIREBALL)).thenReturn(mockResult);

        String jsonBody = """
                {
                    "spell": "FIREBALL"
                }
                """;

        MvcResult mvcResult = mockMvc.perform(post("/api/game/cast-spell")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.remainingMana").value(40))
                .andExpect(jsonPath("$.narrativeStory").value("Boom!"))
                .andReturn();

        CustomReportWatcher.recordDetails(
                "POST /api/game/cast-spell | User: Frodo | Body: {\"spell\": \"FIREBALL\"}",
                "HTTP 200 OK | remainingMana: 40",
                "HTTP " + mvcResult.getResponse().getStatus() + " | Body: " + mvcResult.getResponse().getContentAsString()
        );
    }

    /**
     * Verifies that when {@link GameService} throws a {@link RuntimeException} due to insufficient mana,
     * the {@link com.example.projectdnd.exception.GlobalExceptionHandler} translates it into an
     * HTTP 400 Bad Request JSON payload.
     *
     * @throws Exception if the MockMvc request fails
     */
    @Test
    @DisplayName("Cast Test Failed - Not Enough Mana returns 400 Bad Request")
    @WithMockUser(username = "Frodo")
    void castTestNotEnoughMana() throws Exception {
        when(gameService.castAbility("Frodo", Magic.FIREBALL))
                .thenThrow(new RuntimeException("Not enough mana!"));

        String jsonBody = """
                {
                    "spell": "FIREBALL"
                }
                """;

        MvcResult mvcResult = mockMvc.perform(post("/api/game/cast-spell")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request / Game Logic Rule"))
                .andExpect(jsonPath("$.message").value("Not enough mana!"))
                .andReturn();

        CustomReportWatcher.recordDetails(
                "POST /api/game/cast-spell | User: Frodo (Low Mana)",
                "HTTP 400 Bad Request | message: 'Not enough mana!'",
                "HTTP " + mvcResult.getResponse().getStatus() + " | Body: " + mvcResult.getResponse().getContentAsString()
        );
    }

    /**
     * Verifies that an unauthenticated request to {@code POST /api/game/rest} is rejected
     * with an HTTP 4xx client error (401/403) and never invokes {@link GameService}.
     *
     * @throws Exception if the MockMvc request fails
     */
    @Test
    @DisplayName("Rest Test - Unauthenticated user is blocked with 401/403")
    void restTestUnauthenticated() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/game/rest")
                        .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andReturn();

        CustomReportWatcher.recordDetails(
                "POST /api/game/rest | User: Anonymous (No Auth)",
                "HTTP 4xx Client Error (401/403)",
                "HTTP " + mvcResult.getResponse().getStatus()
        );

        verifyNoInteractions(gameService);
    }
}