package by.ratingsystem.service;

import by.ratingsystem.dto.UserCraeteDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.model.Role;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserReadDto create(UserCraeteDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setRole(Role.SELLER);
        user.setEmail(userDto.getEmail());

        return mapToReadDto(userRepository.save(user));
    }

    public List<UserReadDto> findAll() {
        return userRepository.findAll()
                .stream().map(this::mapToReadDto).toList();
    }

    public UserReadDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapToReadDto(user);
    }

    public UserReadDto update(UserReadDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return mapToReadDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
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
