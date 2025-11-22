package by.ratingsystem.integration.service;

import by.ratingsystem.model.Game;
import by.ratingsystem.model.GameObject;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.repository.GameObjectRepository;
import by.ratingsystem.repository.GameRepository;
import by.ratingsystem.service.GameObjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GameObjectServiceTest {
    @Autowired
    private GameObjectService gameObjectService;

    @Autowired
    private GameObjectRepository gameObjectRepository;

    @Autowired
    private GameRepository gameRepository;

    private static final Long EXISTING_GAME_OBJECT_ID = 1L;
    private static final Long EXISTING_GAME_ID = 1L;
    private static final Long EXISTING_SELLER_ID = 1L;
    private static final String TITLE = "Title";

    @ParameterizedTest(name = "{index}: id = {0}")
    @DisplayName("Failed: Game object deletion with not existing id")
    @ValueSource(longs = {111L, 222L})
    void deleteGameObjectFailedWithNotFoundTest(Long id) {
        assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.delete(id)
        );
    }

    @Test
    @DisplayName("Succeeded: Game object deletion with Game deletion (no related Game objects left)")
    void deleteGameObjectAndDeleteGameSucceededTest() {
        gameObjectService.delete(EXISTING_GAME_OBJECT_ID);

        assertFalse(gameObjectRepository.existsById(EXISTING_GAME_OBJECT_ID), "Game object should not exist");
        assertFalse(gameRepository.existsById(EXISTING_GAME_ID), "Game should not exist");
    }

    @Test
    @DisplayName("Succeeded: Game object deletion without Game deletion (Related game objects remain)")
    void deleteGameObjectSucceededTest() {
        Game game = gameRepository.findById(EXISTING_GAME_ID).get();

        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setId(EXISTING_SELLER_ID);

        GameObject gameObject = new GameObject();
        gameObject.setGame(game);
        gameObject.setTitle(TITLE);
        gameObject.setSeller(sellerProfile);
        gameObjectRepository.save(gameObject);

        gameObjectService.delete(EXISTING_GAME_OBJECT_ID);

        assertFalse(gameObjectRepository.existsById(EXISTING_GAME_OBJECT_ID), "Game object should not exist");
        assertTrue(gameRepository.findById(EXISTING_GAME_ID).isPresent(),
                "Game should exist: related game objects remain");
    }
}
