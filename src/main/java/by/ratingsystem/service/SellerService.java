package by.ratingsystem.service;

import by.ratingsystem.dto.SellerProfileCreateDto;
import by.ratingsystem.dto.SellerProfileReadDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.exception.DuplicateEntityException;
import by.ratingsystem.model.Rating;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.Status;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.SellerProfileRepository;
import by.ratingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SellerService {
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    public SellerService(SellerProfileRepository sellerProfileRepository, UserRepository userRepository) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SellerProfileReadDto create(SellerProfileCreateDto seller, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getSellerProfile() != null) {
            throw new IllegalStateException("Seller Profile already exists");
        }

        if (sellerProfileRepository.existsByNickname(seller.getNickname())) {
            throw new DuplicateEntityException("Duplicate seller nickname");
        }

        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setNickname(seller.getNickname());
        if (seller.getDescription() != null && !seller.getDescription().isBlank()) {
            sellerProfile.setDescription(seller.getDescription().trim());
        }
        sellerProfile.setStatus(Status.PENDING);
        sellerProfile.setUser(user);

        Rating rating = new Rating();
        rating.setAvgRating(BigDecimal.valueOf(0));
        rating.setCommentsCount(0);
        rating.setRatingSum(0);
        rating.setSeller(sellerProfile);

        sellerProfile.setRating(rating);

        return mapToReadDto(sellerProfileRepository.save(sellerProfile));
    }

    @Transactional(readOnly = true)
    public List<SellerProfileReadDto> findAll(Status status) {
        List<SellerProfile> sellerProfiles;
        if (status != null) {
            sellerProfiles = sellerProfileRepository.findAllByStatus(status);
        } else {
            sellerProfiles = sellerProfileRepository.findAll();
        }

        return sellerProfiles.stream().map(this::mapToReadDto).toList();
    }

    @Transactional
    public SellerProfileReadDto changeStatus(Long id,  Status status) {
        SellerProfile sellerProfile = sellerProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        sellerProfile.setStatus(status);
        return mapToReadDto(sellerProfileRepository.save(sellerProfile));
    }

    private SellerProfileReadDto mapToReadDto(SellerProfile sellerProfile) {
        return new SellerProfileReadDto(
                sellerProfile.getId(),
                new UserReadDto(
                        sellerProfile.getUser().getId(),
                        sellerProfile.getUser().getFirstName(),
                        sellerProfile.getUser().getLastName(),
                        sellerProfile.getUser().getEmail(),
                        sellerProfile.getUser().getCreatedAt(),
                        sellerProfile.getUser().isVerified()
                ),
                sellerProfile.getNickname(),
                sellerProfile.getDescription(),
                sellerProfile.getStatus().name(),
                sellerProfile.getCreatedAt()
        );
    }
}
