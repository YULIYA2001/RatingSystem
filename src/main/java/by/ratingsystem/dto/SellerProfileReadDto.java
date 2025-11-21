package by.ratingsystem.dto;

import java.time.LocalDateTime;

public class SellerProfileReadDto extends ShortSellerProfileReadDto {
    private Long userId;
    private String description;
    private LocalDateTime createdDate;
    private String avgRating;
    private int reviewsCount;

    public SellerProfileReadDto(Long id, String nickname, Long userId, String description, LocalDateTime createdDate, String avgRating, int reviewsCount) {
        super(id, nickname);
        this.userId = userId;
        this.description = description;
        this.createdDate = createdDate;
        this.avgRating = avgRating;
        this.reviewsCount = reviewsCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }
}
