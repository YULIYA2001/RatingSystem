package by.ratingsystem.service;

import by.ratingsystem.dto.seller.SellerProfileCreateDto;
import by.ratingsystem.dto.seller.SellerProfileAdminReadDto;
import by.ratingsystem.dto.seller.SellerProfileReadDto;
import by.ratingsystem.dto.user.UserReadDto;
import by.ratingsystem.exception.DuplicateEntityException;
import by.ratingsystem.model.Rating;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.User;
import by.ratingsystem.model.enums.Status;
import by.ratingsystem.repository.SellerProfileRepository;
import by.ratingsystem.specification.SellerProfileSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserService userService;
    private final RatingService ratingService;

    @Autowired
    public SellerService(SellerProfileRepository sellerProfileRepository,
                         UserService userService,
                         RatingService ratingService) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @Transactional
    public SellerProfileAdminReadDto createSellerProfile(SellerProfileCreateDto seller, Long userId) {
        SellerProfile sellerProfile = create(seller, userId);
        return mapToFullReadDto(sellerProfile);
    }

    public SellerProfile create(SellerProfileCreateDto seller, Long userId) {
        User user = null;
        if (userId != null) {
            user = userService.getById(userId);

            if (user.getSellerProfile() != null) {
                throw new DuplicateEntityException("Seller Profile for user already exists");
            }
        }

        if (sellerProfileRepository.existsByNickname(seller.getNickname())) {
            throw new DuplicateEntityException("Duplicate seller nickname");
        }

        SellerProfile sellerProfile = getSellerProfile(seller, userId, user);

        return sellerProfileRepository.save(sellerProfile);
    }

    private SellerProfile getSellerProfile(SellerProfileCreateDto seller, Long userId, User user) {
        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setNickname(seller.getNickname());
        if (seller.getDescription() != null && !seller.getDescription().isBlank()) {
            sellerProfile.setDescription(seller.getDescription().trim());
        }
        sellerProfile.setStatus(Status.PENDING);
        if (userId != null) {
            sellerProfile.setUser(user);
        }

        Rating rating = ratingService.getEmptyRating();
        rating.setSeller(sellerProfile);
        sellerProfile.setRating(rating);

        return sellerProfile;
    }

    @Transactional(readOnly = true)
    public List<? extends SellerProfileReadDto> findAll(Status status,
                                                        BigDecimal minRating,
                                                        BigDecimal maxRating,
                                                        Long gameId,
                                                        Pageable pageable,
                                                        boolean isAdmin) {
        Specification<SellerProfile> spec = SellerProfileSpecification.withFilters(
                status, minRating, maxRating, gameId);

        Page<SellerProfile> sellerProfiles = sellerProfileRepository.findAll(spec, pageable);

        if (isAdmin) {
            return sellerProfiles.stream().map(this::mapToFullReadDto).toList();
        }

        return sellerProfiles.stream().map(this::mapToReadDto).toList();
    }

    @Transactional
    public SellerProfileAdminReadDto approveSellerProfile(Long id, Status status) {
        SellerProfile sellerProfile = changeStatus(id, status);
        sellerProfile.getComments().forEach(comment -> comment.setVerifiedSeller(true));
        return mapToFullReadDto(sellerProfile);
    }

    @Transactional
    public SellerProfileAdminReadDto rejectSellerProfile(Long id, Status status) {
        SellerProfile sellerProfile = changeStatus(id, status);
        sellerProfile.getComments().forEach(comment -> comment.setStatus(Status.REJECTED));
        return mapToFullReadDto(sellerProfile);
    }

    @Transactional(readOnly = true)
    public List<SellerProfileAdminReadDto> findTopRatingSellers(Integer topCount) {
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

    public SellerProfile findByUserId(Long userId) {
        return sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Seller with id=%d not found".formatted(userId)));
    }

    private SellerProfile changeStatus(Long id, Status status) {
        SellerProfile sellerProfile = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller with id=%d not found".formatted(id)));
        sellerProfile.setStatus(status);
        return sellerProfile;
    }

    private SellerProfileAdminReadDto mapToFullReadDto(SellerProfile sellerProfile) {
        return new SellerProfileAdminReadDto(
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
}
