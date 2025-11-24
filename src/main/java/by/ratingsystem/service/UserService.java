package by.ratingsystem.service;

import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.model.User;

import java.util.List;

public interface UserService {
    List<UserReadDto> findAll();

    UserReadDto findById(Long id);

    User findByEmail(String email);

    void delete(Long id);

    UserReadDto update(Long userId, UserReadDto userDto);
}
