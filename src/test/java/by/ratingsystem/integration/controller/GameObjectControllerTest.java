package by.ratingsystem.integration.controller;

import by.ratingsystem.dto.gameobject.GameDto;
import by.ratingsystem.dto.gameobject.GameObjectCreateDto;
import by.ratingsystem.model.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class GameObjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private static final int EXPECTED_GAMES_COUNT = 1;
    private static final Long NOT_EXISTED_GAME_OBJECT_ID = 999L;
    private static final String NEW_TITLE = "New title";
    private static final String NEW_DESCRIPTION = "New description";
    private static final Game EXISTED_GAME = buildExistedGame();

    private static Game buildExistedGame() {
        Game game = new Game();
        game.setId(1L);
        game.setName("Test game");
        game.setDescription("Test game description");
        return game;
    }


    @Test
    @DisplayName("Get all games")
    void getGamesTest() throws Exception {
        mockMvc.perform(get("/object/games"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(EXPECTED_GAMES_COUNT))
                .andExpect(jsonPath("$[0].id").value(EXISTED_GAME.getId()))
                .andExpect(jsonPath("$[0].name").value(EXISTED_GAME.getName()))
                .andExpect(jsonPath("$[0].description").value(EXISTED_GAME.getDescription()));
    }

    @ParameterizedTest(name = "{index}: id = {0}, expectedStatus = {1}")
    @CsvSource({
            "1, 200",
            "999, 404"
    })
    @DisplayName("Delete game object")
    void deleteGameObjectTest(Long id, int expectedStatus) throws Exception {
        mockMvc.perform(delete("/object/{id}", id))
                .andDo(print())
                .andExpect(status().is(expectedStatus));
    }

    @Test
    @DisplayName("Update not existed game object")
    void updateGameObjectFailedNotFoundTest() throws Exception {
        mockMvc.perform(put("/object/{id}", NOT_EXISTED_GAME_OBJECT_ID)
                        .content(asJsonString(
                                new GameObjectCreateDto(
                                        NEW_TITLE,
                                        NEW_DESCRIPTION,
                                        new GameDto(EXISTED_GAME.getId(), null, null)
                                )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
