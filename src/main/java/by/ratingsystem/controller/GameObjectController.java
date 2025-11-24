package by.ratingsystem.controller;

import by.ratingsystem.dto.gameobject.GameDto;
import by.ratingsystem.dto.gameobject.GameObjectCreateDto;
import by.ratingsystem.dto.gameobject.GameObjectReadDto;
import by.ratingsystem.security.jwt.JwtUserDetails;
import by.ratingsystem.service.GameObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/object")
public class GameObjectController {
    private final GameObjectService gameObjectService;

    @Autowired
    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GameObjectReadDto> create(@RequestBody GameObjectCreateDto gameObjectCreateDto,
                                                    @AuthenticationPrincipal JwtUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        return new ResponseEntity<>(gameObjectService.create(userId, gameObjectCreateDto), HttpStatus.CREATED);
    }

    @GetMapping("/games")
    public ResponseEntity<List<GameDto>> getGames() {
        return new ResponseEntity<>(gameObjectService.findAllExistingGames(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GameObjectReadDto>> getObjects() {
        return new ResponseEntity<>(gameObjectService.findAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GameObjectReadDto> update(@PathVariable Long id,
                                                    @RequestBody GameObjectCreateDto gameObjectDto,
                                                    @AuthenticationPrincipal JwtUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        return new ResponseEntity<>(gameObjectService.update(userId, id, gameObjectDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id,
                                             @AuthenticationPrincipal JwtUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        gameObjectService.delete(userId, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
