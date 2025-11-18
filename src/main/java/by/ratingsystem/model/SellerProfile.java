package by.ratingsystem.model;

import by.ratingsystem.model.base.TimestampedEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "seller_profiles")
public class SellerProfile extends TimestampedEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL, optional = false)
    private Rating rating;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GameObject> gameObjects;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<Comment> comments;
}
