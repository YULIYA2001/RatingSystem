package by.ratingsystem.specification;

import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SellerProfileSpecification {
    public static Specification<SellerProfile> withFilters(
            Status status,
            BigDecimal minRating,
            BigDecimal maxRating,
            Long gameId) {

        return withBasicFilters(status, minRating, maxRating).and(withGameFilter(gameId));
    }

    private static Specification<SellerProfile> withBasicFilters(
            Status status,
            BigDecimal minRating,
            BigDecimal maxRating) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating").get("avgRating"), minRating));
            }

            if (maxRating != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating").get("avgRating"), maxRating));
            }

            query.orderBy(
                    cb.desc(root.get("rating").get("avgRating")),
                    cb.desc(root.get("rating").get("commentsCount"))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<SellerProfile> withGameFilter(Long gameId) {
        return (root, query, cb) -> {
            if (gameId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.join("gameObjects").join("game").get("id"), gameId);
        };
    }

    private SellerProfileSpecification() {}
}
