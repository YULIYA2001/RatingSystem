package by.ratingsystem.service;

import by.ratingsystem.dto.SellerProfileCreateDto;
import by.ratingsystem.dto.SellerProfileFullReadDto;
import by.ratingsystem.dto.SellerProfileReadDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.exception.DuplicateEntityException;
import by.ratingsystem.model.Rating;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.Status;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.SellerProfileRepository;
import by.ratingsystem.repository.UserRepository;
import by.ratingsystem.specification.SellerProfileSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public SellerProfileFullReadDto createSellerProfile(SellerProfileCreateDto seller, Long userId) {
        return mapToFullReadDto(create(seller, userId));
    }

    public SellerProfile create(SellerProfileCreateDto seller, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

            if (user.getSellerProfile() != null) {
                throw new DuplicateEntityException("Seller Profile for user already exists");
            }
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
        if (userId != null) {
            sellerProfile.setUser(user);
        }

        Rating rating = new Rating();
        rating.setAvgRating(BigDecimal.valueOf(0));
        rating.setCommentsCount(0);
        rating.setRatingSum(0);
        rating.setSeller(sellerProfile);

        sellerProfile.setRating(rating);

        return sellerProfileRepository.save(sellerProfile);
    }

    @Transactional(readOnly = true)
    public List<? extends SellerProfileReadDto> findAll(Status status, BigDecimal  minRating, BigDecimal  maxRating,
                                                        Long gameId, Pageable pageable, boolean isAdmin) {
        Specification<SellerProfile> spec = SellerProfileSpecification.withFilters(
                status, minRating, maxRating, gameId);

        Page<SellerProfile> sellerProfiles = sellerProfileRepository.findAll(spec, pageable);

        if (isAdmin) {
            return sellerProfiles.stream().map(this::mapToFullReadDto).toList();
        }

        return sellerProfiles.stream().map(this::mapToReadDto).toList();
    }

    private SellerProfile changeStatus(Long id, Status status) {
        SellerProfile sellerProfile = sellerProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        sellerProfile.setStatus(status);
        return sellerProfile;
    }

    @Transactional
    public SellerProfileFullReadDto approveSellerProfile(Long id, Status status) {
        SellerProfile sellerProfile = changeStatus(id, status);
        sellerProfile.getComments().forEach(comment -> comment.setVerifiedSeller(true));
        return mapToFullReadDto(sellerProfile);
    }

    @Transactional
    public SellerProfileFullReadDto rejectSellerProfile(Long id, Status status) {
        SellerProfile sellerProfile = changeStatus(id, status);
        sellerProfile.getComments().forEach(comment -> comment.setStatus(Status.REJECTED));
        return mapToFullReadDto(sellerProfile);
    }

    private SellerProfileFullReadDto mapToFullReadDto(SellerProfile sellerProfile) {
        return new SellerProfileFullReadDto(
                sellerProfile.getId(),
                sellerProfile.getNickname(),
                null,
                sellerProfile.getDescription(),
                sellerProfile.getCreatedAt(),
                sellerProfile.getRating().getAvgRating().toString(),
                sellerProfile.getRating().getCommentsCount(),
                sellerProfile.getUser() == null ? new UserReadDto() : new UserReadDto(
                        sellerProfile.getUser().getId(),
                        sellerProfile.getUser().getFirstName(),
                        sellerProfile.getUser().getLastName(),
                        sellerProfile.getUser().getEmail(),
                        sellerProfile.getUser().getCreatedAt(),
                        sellerProfile.getUser().isVerified()
                ),
                sellerProfile.getStatus().name()
        );
    }

    private SellerProfileReadDto mapToReadDto(SellerProfile sellerProfile) {
        return new SellerProfileReadDto(
                sellerProfile.getId(),
                sellerProfile.getNickname(),
                sellerProfile.getUser() == null ? null : sellerProfile.getUser().getId(),
                sellerProfile.getDescription(),
                sellerProfile.getCreatedAt(),
                sellerProfile.getRating().getAvgRating().toString(),
                sellerProfile.getRating().getCommentsCount()
        );
    }

    @Transactional(readOnly = true)
    public List<SellerProfileFullReadDto> findTopRatingSellers(Integer topCount) {
        List<SellerProfile> topCountSellers;
        if (topCount == null) {
            topCountSellers = sellerProfileRepository
                    .findAllByOrderByRating_AvgRatingDesc();
        } else {
            topCountSellers = sellerProfileRepository
                    .findAllByOrderByRating_AvgRatingDesc(PageRequest.of(0, topCount))
                    .getContent();
        }
        return topCountSellers.stream().map(this::mapToFullReadDto).toList();
    }
}
