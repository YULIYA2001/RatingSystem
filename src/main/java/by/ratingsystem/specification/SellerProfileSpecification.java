package by.ratingsystem.specification;

import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.enums.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SellerProfileSpecification {
    private static final String RATING = "rating";
    private static final String AVG_RATING = "avgRating";
    private static final String STATUS = "status";
    private static final String COMMENTS_COUNT = "commentsCount";
    private static final String GAME_OBJECTS = "gameObjects";
    private static final String GAME = "game";
    private static final String ID = "id";

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
                predicates.add(cb.equal(root.get(STATUS), status));
            }

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(RATING).get(AVG_RATING), minRating));
            }

            if (maxRating != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(RATING).get(AVG_RATING), maxRating));
            }

            if (query != null) {
                query.orderBy(
                        cb.desc(root.get(RATING).get(AVG_RATING)),
                        cb.desc(root.get(RATING).get(COMMENTS_COUNT))
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<SellerProfile> withGameFilter(Long gameId) {
        return (root, _, cb) -> {
            if (gameId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.join(GAME_OBJECTS).join(GAME).get(ID), gameId);
        };
    }

    private SellerProfileSpecification() {}
}
