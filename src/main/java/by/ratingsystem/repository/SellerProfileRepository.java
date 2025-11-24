package by.ratingsystem.repository;

import by.ratingsystem.model.SellerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerProfileRepository
        extends JpaRepository<SellerProfile, Long>, JpaSpecificationExecutor<SellerProfile> {
    Optional<SellerProfile> findByUserId(Long userId);
    boolean existsByNickname(String nickname);
    Page<SellerProfile> findAllByOrderByRating_AvgRatingDesc(Pageable pageable);
    List<SellerProfile> findAllByOrderByRating_AvgRatingDesc();
}
