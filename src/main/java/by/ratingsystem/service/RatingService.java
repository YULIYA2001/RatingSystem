package by.ratingsystem.service;

import by.ratingsystem.model.Rating;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RatingService {
    public void reduceAndSaveRating(Rating rating, int oldCommentRating) {
        recalculateAndSaveRating(rating, -oldCommentRating, -1);
    }

    public void increaseAndSaveRating(Rating rating, int oldCommentsSumRating, int oldCommentsCount) {
        recalculateAndSaveRating(rating, +oldCommentsSumRating, +oldCommentsCount);
    }

    private void recalculateAndSaveRating(Rating rating, int oldCommentsSumRating, int oldCommentsCount) {
        int newSum = rating.getRatingSum() + oldCommentsSumRating;
        int newCount = rating.getCommentsCount() + oldCommentsCount;
        BigDecimal newAvg = BigDecimal.valueOf(newSum).divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP);

        rating.setRatingSum(newSum);
        rating.setCommentsCount(newCount);
        rating.setAvgRating(newAvg);
    }
}
