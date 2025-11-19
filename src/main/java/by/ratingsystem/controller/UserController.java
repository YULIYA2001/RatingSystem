package by.ratingsystem.controller;

import by.ratingsystem.dto.UserCraeteDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.service.UserService;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserReadDto> createUser(@RequestBody UserCraeteDto user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @GetMapping
    // @PreAuthorize(ADMIN)
    public ResponseEntity<List<UserReadDto>> getUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // @PreAuthorize(ADMIN)
    public ResponseEntity<UserReadDto> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PutMapping
    // @PreAuthorize(USER current)
    public ResponseEntity<UserReadDto> updateUser(@RequestBody UserReadDto user) {
        return new ResponseEntity<>(userService.update(user),  HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize(ADMIN)
    public HttpStatus deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return HttpStatus.OK;
    }
}
