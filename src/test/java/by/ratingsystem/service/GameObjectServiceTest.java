package by.ratingsystem.service;

import by.ratingsystem.dto.GameDto;
import by.ratingsystem.dto.GameObjectCreateDto;
import by.ratingsystem.dto.GameObjectReadDto;
import by.ratingsystem.exception.DuplicateEntityException;
import by.ratingsystem.model.Game;
import by.ratingsystem.model.GameObject;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.repository.GameObjectRepository;
import by.ratingsystem.repository.GameRepository;
import by.ratingsystem.repository.SellerProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameObjectServiceTest {
    @Mock
    private GameObjectRepository gameObjectRepository;

    @Mock
    private SellerProfileRepository sellerProfileRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameObjectService gameObjectService;

    private static final int POSITIVE_NUMBER = 1;
    private static final int ZERO = 0;
    private static final Long ANY_ID = 1L;
    private static final String ANY_STRING = "";

    @Test
    void createGameObjectFailedWithSellerNotFoundTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(true);

        when(sellerProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.create(userId, gameObjectCreateDto)
        );
    }

    @Test
    void createGameObjectFailedWithExistingGameNotFoundTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(true);

        SellerProfile existedSellerProfile = new SellerProfile();

        when(sellerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existedSellerProfile));
        when(gameRepository.findById(gameObjectCreateDto.getGame().getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.create(userId, gameObjectCreateDto)
        );
    }

    @Test
    void createGameObjectSucceededWithExistingGameTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(true);

        SellerProfile existedSellerProfile = new SellerProfile();
        Game existedGame = buildGame();

        GameObject savedGameObject = buildGameObject(ANY_ID);

        when(sellerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existedSellerProfile));
        when(gameRepository.findById(gameObjectCreateDto.getGame().getId())).thenReturn(Optional.of(existedGame));
        when(gameObjectRepository.save(any(GameObject.class))).thenReturn(savedGameObject);

        GameObjectReadDto result = gameObjectService.create(userId, gameObjectCreateDto);
        assertNotNull(result);

        verify(gameObjectRepository).save(any(GameObject.class));
    }

    @Test
    void createGameObjectAndGameFailedWithDuplicateGameNameTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(false);

        SellerProfile existedSellerProfile = new SellerProfile();

        when(sellerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existedSellerProfile));
        when(gameRepository.existsByName(gameObjectCreateDto.getGame().getName())).thenReturn(true);

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> gameObjectService.create(userId, gameObjectCreateDto)
        );
    }

    @Test
    void createGameObjectAndGameSucceededTest() {
        Long userId = ANY_ID;
        GameObjectCreateDto gameObjectCreateDto = buildGameObjectDtoWithGame(false);

        SellerProfile existedSellerProfile = new SellerProfile();

        GameObject savedGameObject = buildGameObject(ANY_ID);

        when(sellerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existedSellerProfile));
        when(gameRepository.existsByName(gameObjectCreateDto.getGame().getName())).thenReturn(false);
        when(gameObjectRepository.save(any(GameObject.class))).thenReturn(savedGameObject);

        GameObjectReadDto result = gameObjectService.create(userId, gameObjectCreateDto);
        assertNotNull(result);

        verify(gameObjectRepository).save(any(GameObject.class));
    }

    @Test
    void deleteGameObjectFailedWithNotFoundTest() {
        Long id = ANY_ID;

        when(gameObjectRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> gameObjectService.delete(id)
        );
    }

    @Test
    void deleteGameObjectAndDeleteGameSucceededTest() {
        Long id = ANY_ID;

        GameObject existed = buildGameObject(id);
        Game existedGame = existed.getGame();

        when(gameObjectRepository.findById(id)).thenReturn(Optional.of(existed));
        when(gameObjectRepository.countByGameId(existedGame.getId())).thenReturn(POSITIVE_NUMBER);

        gameObjectService.delete(id);

        verify(gameObjectRepository).deleteById(id);
        verify(gameRepository, never()).deleteById(any());
    }

    @Test
    void deleteGameObjectSucceededTest() {
        Long id = ANY_ID;

        GameObject existed = buildGameObject(id);
        Game existedGame = existed.getGame();

        when(gameObjectRepository.findById(id)).thenReturn(Optional.of(existed));
        when(gameObjectRepository.countByGameId(existedGame.getId())).thenReturn(ZERO);

        gameObjectService.delete(id);

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

    private GameObject buildGameObject(Long objectId) {
        Game game = buildGame();

        GameObject gameObject = new GameObject();
        gameObject.setId(objectId);
        gameObject.setTitle(ANY_STRING);
        gameObject.setDescription(ANY_STRING);
        gameObject.setSeller(new SellerProfile());
        gameObject.setGame(game);

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