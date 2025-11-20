package by.ratingsystem.service;

import by.ratingsystem.model.Rating;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RatingService {

    public void recalculateAndSaveRating(Rating rating, int oldCommentRating) {
        int newSum = rating.getRatingSum() - oldCommentRating;
        int newCount = rating.getCommentsCount() - 1;
        BigDecimal newAvg = BigDecimal.valueOf(newSum).divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP);

        rating.setRatingSum(newSum);
        rating.setCommentsCount(newCount);
        rating.setAvgRating(newAvg);
    }
}
