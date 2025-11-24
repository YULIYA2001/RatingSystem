package by.ratingsystem.unit.service;

import by.ratingsystem.dto.gameobject.GameDto;
import by.ratingsystem.dto.gameobject.GameObjectCreateDto;
import by.ratingsystem.dto.gameobject.GameObjectReadDto;
import by.ratingsystem.exception.DuplicateEntityException;
import by.ratingsystem.model.Game;
import by.ratingsystem.model.GameObject;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.GameObjectRepository;
import by.ratingsystem.repository.GameRepository;
import by.ratingsystem.service.GameObjectService;
import by.ratingsystem.service.SellerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameObjectServiceTest {
    @Mock
    private GameObjectRepository gameObjectRepository;

    @Mock
    private SellerService sellerService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameObjectService gameObjectService;

    private static final int POSITIVE_NUMBER = 1;
    private static final int ZERO = 0;
    private static final Long ANY_ID = 1L;
    private static final String ANY_STRING = "";

    @Test
    @Order(1)
    @DisplayName("Failed: Game object creation by user without Seller profile")
    void createGameObjectFailedWithSellerNotFoundTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(true);
        String expMessage = "Seller profile with id=%d not found".formatted(userId);

        when(sellerService.findByUserId(userId))
                .thenThrow(new EntityNotFoundException(expMessage));

        EntityNotFoundException result = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.create(userId, gameObjectCreateDto)
        );

        assertEquals(expMessage, result.getMessage());
    }

    @Test
    @Order(1)
    @DisplayName("Failed: Game object creation by user with seller profile but for not existing Game")
    void createGameObjectFailedWithExistingGameNotFoundTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(true);

        SellerProfile existedSellerProfile = new SellerProfile();

        when(sellerService.findByUserId(userId)).thenReturn(existedSellerProfile);
        when(gameRepository.findById(gameObjectCreateDto.getGame().getId())).thenReturn(Optional.empty());

        Exception result = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.create(userId, gameObjectCreateDto)
        );

        assertEquals("Game with id=%d not found".formatted(gameObjectCreateDto.getGame().getId()), result.getMessage());
    }

    @Test
    @Order(1)
    @DisplayName("Succeeded: Game object creation by user with seller profile and for existing Game")
    void createGameObjectSucceededWithExistingGameTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(true);

        SellerProfile existedSellerProfile = new SellerProfile();
        Game existedGame = buildGame();

        GameObject savedGameObject = buildGameObject(ANY_ID, any());

        when(sellerService.findByUserId(userId)).thenReturn(existedSellerProfile);
        when(gameRepository.findById(gameObjectCreateDto.getGame().getId())).thenReturn(Optional.of(existedGame));
        when(gameObjectRepository.save(any(GameObject.class))).thenReturn(savedGameObject);

        GameObjectReadDto result = gameObjectService.create(userId, gameObjectCreateDto);
        assertNotNull(result);

        verify(gameObjectRepository).save(any(GameObject.class));
    }

    @Test
    @Order(1)
    @DisplayName("Failed: Game object (with new Game) creation by user with seller profile and duplicating Game name")
    void createGameObjectAndGameFailedWithDuplicateGameNameTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(false);

        SellerProfile existedSellerProfile = new SellerProfile();

        when(sellerService.findByUserId(userId)).thenReturn(existedSellerProfile);
        when(gameRepository.existsByName(gameObjectCreateDto.getGame().getName())).thenReturn(true);

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> gameObjectService.create(userId, gameObjectCreateDto)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Succeeded: Game object (with new Game) creation by user with seller profile")
    void createGameObjectAndGameSucceededTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(false);

        SellerProfile existedSellerProfile = new SellerProfile();

        GameObject savedGameObject = buildGameObject(ANY_ID, any());

        when(sellerService.findByUserId(userId)).thenReturn(existedSellerProfile);
        when(gameRepository.existsByName(gameObjectCreateDto.getGame().getName())).thenReturn(false);
        when(gameObjectRepository.save(any(GameObject.class))).thenReturn(savedGameObject);

        GameObjectReadDto result = gameObjectService.create(userId, gameObjectCreateDto);
        assertNotNull(result);

        verify(gameObjectRepository).save(any(GameObject.class));
    }

    @ParameterizedTest(name = "{index}: id = {0}")
    @Order(2)
    @DisplayName("Failed: Game object deletion with not existing id")
    @ValueSource(longs = {1L, 2L})
    void deleteGameObjectFailedWithNotFoundObjTest(Long id) {
        when(gameObjectRepository.findById(id)).thenReturn(Optional.empty());

        Exception result = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.delete(null, id)
        );

        assertEquals("GameObject with id=%d not found".formatted(id),  result.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Failed: Game object deletion with existing id by not owner user")
    void deleteGameObjectFailedWithNotFoundObjForUserTest() {
        Long userId = ANY_ID;
        Long id = ANY_ID;

        GameObject existed = buildGameObject(id, any());

        when(gameObjectRepository.findById(id)).thenReturn(Optional.of(existed));

        Exception result = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.delete(userId, ANY_ID)
        );

        assertEquals("You can delete only your game objects", result.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Succeeded: Game object deletion with Game deletion (no related Game objects left)")
    void deleteGameObjectAndDeleteGameSucceededTest() {
        Long userId = ANY_ID;
        Long id = ANY_ID;

        GameObject existed = buildGameObject(id, userId);
        Game existedGame = existed.getGame();

        when(gameObjectRepository.findById(id)).thenReturn(Optional.of(existed));
        when(gameObjectRepository.countByGameId(existedGame.getId())).thenReturn(POSITIVE_NUMBER);

        gameObjectService.delete(userId, id);

        verify(gameObjectRepository).deleteById(id);
        verify(gameRepository, never()).deleteById(any());
    }

    @Test
    @Order(2)
    @DisplayName("Succeeded: Game object deletion without Game deletion (Related game objects remain)")
    void deleteGameObjectSucceededTest() {
        Long userId = ANY_ID;
        Long id = ANY_ID;

        GameObject existed = buildGameObject(id, userId);
        Game existedGame = existed.getGame();

        when(gameObjectRepository.findById(id)).thenReturn(Optional.of(existed));
        when(gameObjectRepository.countByGameId(existedGame.getId())).thenReturn(ZERO);

        gameObjectService.delete(userId, id);

        verify(gameObjectRepository).deleteById(id);
        verify(gameRepository).deleteById(existedGame.getId());
    }

    private Game buildGame() {
        Game game = new Game();
        game.setId(ANY_ID);
        game.setName(ANY_STRING);
        game.setDescription(ANY_STRING);
        game.setGameObjects(new ArrayList<>());

        return game;
    }

    private GameObject buildGameObject(Long objectId, Long userId) {
        Game game = buildGame();

        User user = new User();
        user.setId(userId);
        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setUser(user);

        GameObject gameObject = new GameObject();
        gameObject.setId(objectId);
        gameObject.setTitle(ANY_STRING);
        gameObject.setDescription(ANY_STRING);
        gameObject.setSeller(new SellerProfile());
        gameObject.setGame(game);
        gameObject.setSeller(sellerProfile);

        game.getGameObjects().add(gameObject);

        return gameObject;
    }

    private GameObjectCreateDto buildGameObjectDtoWithGame(boolean isGameExisting) {
        return new GameObjectCreateDto(
                ANY_STRING,
                ANY_STRING,
                isGameExisting
                        ? new GameDto(ANY_ID, null, null)
                        : new GameDto(null, ANY_STRING, ANY_STRING)
        );
    }
}