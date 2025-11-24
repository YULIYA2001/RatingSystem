package by.ratingsystem.service;

import by.ratingsystem.dto.GameDto;
import by.ratingsystem.dto.GameObjectCreateDto;
import by.ratingsystem.dto.GameObjectReadDto;
import by.ratingsystem.dto.ShortGameDto;
import by.ratingsystem.dto.seller.ShortSellerProfileReadDto;
import by.ratingsystem.exception.DuplicateEntityException;
import by.ratingsystem.model.Game;
import by.ratingsystem.model.GameObject;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.repository.GameObjectRepository;
import by.ratingsystem.repository.GameRepository;
import by.ratingsystem.repository.SellerProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GameObjectService {
    private final GameObjectRepository gameObjectRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final GameRepository gameRepository;

    public GameObjectService(GameObjectRepository gameObjectRepository, SellerProfileRepository sellerProfileRepository, GameRepository gameRepository) {
        this.gameObjectRepository = gameObjectRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public GameObjectReadDto create(Long userId, GameObjectCreateDto gameObjectCreateDto) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("SellerProfile not found"));

        GameObject gameObject = new GameObject();
        gameObject.setTitle(gameObjectCreateDto.getTitle());
        gameObject.setDescription(gameObjectCreateDto.getDescription());
        gameObject.setSeller(sellerProfile);

        Game game = getGameFromGameObjectDto(gameObjectCreateDto);
        game.getGameObjects().add(gameObject);

        gameObject.setGame(game);

        return mapToReadDto(gameObjectRepository.save(gameObject));
    }

    private Game getGameFromGameObjectDto(GameObjectCreateDto gameObjectCreateDto) {
        if (gameObjectCreateDto.getGame().getId() != null) {
            return gameRepository.findById(gameObjectCreateDto.getGame().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        }

        return createNewFromGameDto(gameObjectCreateDto.getGame());
    }

    private Game createNewFromGameDto(GameDto gameDto) {
        if (gameRepository.existsByName(gameDto.getName())) {
            throw new DuplicateEntityException("Duplicate game name");
        }

        Game game = new Game();
        game.setName(gameDto.getName());
        if (gameDto.getDescription() != null
                && !gameDto.getDescription().isBlank()) {
            game.setDescription(gameDto.getDescription());
        }
        game.setGameObjects(new ArrayList<>());
        return game;
    }

    @Transactional(readOnly = true)
    public List<GameObjectReadDto> findAll() {
        return gameObjectRepository.findAll()
                .stream().map(this::mapToReadDto).toList();
    }

    // if we have previous Game -> do nothing
    // if new game and id not found -> exception
    // if new game and id found -> replace game and delete old if it has no other objects
    // if new game and id is null -> replace game and delete old if it has no other objects
    @Transactional
    public GameObjectReadDto update(Long userId, Long id, GameObjectCreateDto gameObjectDto) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GameObject not found"));

        if (!Objects.equals(gameObject.getSeller().getUser().getId(), userId)) {
            throw new EntityNotFoundException("You can modify only your game objects");
        }

        Long oldGameId = null;

        if (!Objects.equals(gameObjectDto.getGame().getId(), gameObject.getGame().getId())) {
            if (gameObject.getGame().getGameObjects().size() == 1) {
                oldGameId = gameObject.getGame().getId();
            }

            Game game = getGameFromGameObjectDto(gameObjectDto);
            game.getGameObjects().add(gameObject);

            gameObject.setGame(game);

        }

        gameObject.setTitle(gameObjectDto.getTitle());
        gameObject.setDescription(gameObjectDto.getDescription());

        gameObject = gameObjectRepository.saveAndFlush(gameObject);

        if (oldGameId != null) {
            gameRepository.deleteById(oldGameId);
        }

        return mapToReadDto(gameObject);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GameObject with id {%s} not found".formatted(id)));

        if (!Objects.equals(gameObject.getSeller().getUser().getId(), userId)) {
            throw new EntityNotFoundException("You can delete only your game objects");
        }

        Long gameId = gameObject.getGame().getId();
        gameObjectRepository.deleteById(id);

        if (gameObjectRepository.countByGameId(gameId) == 0) {
            gameRepository.deleteById(gameId);
        }
    }

    private GameObjectReadDto mapToReadDto(GameObject gameObject) {
        return new GameObjectReadDto(
                gameObject.getId(),
                gameObject.getTitle(),
                gameObject.getDescription(),
                new ShortSellerProfileReadDto(
                        gameObject.getSeller().getId(),
                        gameObject.getSeller().getNickname()
                ),
                new ShortGameDto(
                        gameObject.getGame().getId(),
                        gameObject.getGame().getName()
                ),
                gameObject.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<GameDto> findAllExistingGames() {
        return gameRepository.findAll().stream().map(game -> new GameDto(
                game.getId(),
                game.getName(),
                game.getDescription()
        )).toList();
    }
}
