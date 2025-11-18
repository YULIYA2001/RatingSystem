package by.ratingsystem.model;

import by.ratingsystem.model.base.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_objects")
public class GameObject extends TimestampedEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
}
