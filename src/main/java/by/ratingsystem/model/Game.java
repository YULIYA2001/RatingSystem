package by.ratingsystem.model;

import by.ratingsystem.model.base.EntityWithCreationDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "games")
public class Game extends EntityWithCreationDate {
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private List<GameObject> gameObjects;
}
