package by.ratingsystem.service;

import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserReadDto> findAll() {
        return userRepository.findAll()
                .stream().map(this::mapToReadDto).toList();
    }

    public UserReadDto findById(Long userId) {
        User user = getById(userId);
        return mapToReadDto(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public UserReadDto update(Long userId, UserReadDto userDto) {
        User user = getById(userId);

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return mapToReadDto(userRepository.save(user));
    }

    private User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
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
