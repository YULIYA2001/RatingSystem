package by.ratingsystem.model;

import by.ratingsystem.model.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "ratings")
public class Rating extends BaseEntity {
    @Column(name = "avg_rating", nullable = false)
    private BigDecimal avgRating;

    @Column(name = "rating_sum", nullable = false)
    private int ratingSum;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    public BigDecimal getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(BigDecimal avgRating) {
        this.avgRating = avgRating;
    }

    public int getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(int ratingSum) {
        this.ratingSum = ratingSum;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public SellerProfile getSeller() {
        return seller;
    }

    public void setSeller(SellerProfile seller) {
        this.seller = seller;
    }
}
