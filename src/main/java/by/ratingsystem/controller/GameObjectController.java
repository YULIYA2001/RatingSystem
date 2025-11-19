package by.ratingsystem.controller;

import by.ratingsystem.dto.GameObjectCreateDto;
import by.ratingsystem.dto.GameObjectReadDto;
import by.ratingsystem.service.GameObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @PostMapping
//    @PreAuthorize(USER current)
    public ResponseEntity<GameObjectReadDto> create(@RequestBody GameObjectCreateDto gameObjectCreateDto) {
        // get authorized userId
        Long userId = 3L;
        return new ResponseEntity<>(gameObjectService.create(userId, gameObjectCreateDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GameObjectReadDto>> getObjects() {
        return new ResponseEntity<>(gameObjectService.findAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
//    @PreAuthorize(USER current)
    public ResponseEntity<GameObjectReadDto> update(@PathVariable Long id,
                                                    @RequestBody GameObjectCreateDto gameObjectDto) {
        return new ResponseEntity<>(gameObjectService.update(id, gameObjectDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize(USER current)
    public HttpStatus delete(@PathVariable Long id) {
        gameObjectService.delete(id);
        return HttpStatus.OK;
    }
}
