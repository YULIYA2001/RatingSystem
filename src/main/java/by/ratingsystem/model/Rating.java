package by.ratingsystem.model;

import by.ratingsystem.model.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ratings")
public class Rating extends BaseEntity {
    @Column(name = "avg_rating", nullable = false)
    private double avgRating;

    @Column(name = "rating_sum", nullable = false)
    private int ratingSum;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;
}
