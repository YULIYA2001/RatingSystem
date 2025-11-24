package by.ratingsystem.service.impl;

import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.UserRepository;
import by.ratingsystem.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserReadDto> findAll() {
        return userRepository.findAll()
                .stream().map(this::mapToReadDto).toList();
    }

    @Override
    public UserReadDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapToReadDto(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserReadDto update(Long userId, UserReadDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return mapToReadDto(userRepository.save(user));
    }

    private UserReadDto mapToReadDto(User user) {
        return new UserReadDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.isVerified()
        );
    }
}
