package by.ratingsystem.dto.gameobject;

import by.ratingsystem.dto.seller.ShortSellerProfileReadDto;

import java.time.LocalDateTime;

public class GameObjectReadDto {
    private Long id;
    private String title;
    private String description;
    private ShortSellerProfileReadDto seller;
    private ShortGameDto game;
    private LocalDateTime lastUpdate;

    public GameObjectReadDto(Long id, String title, String description, ShortSellerProfileReadDto seller,
                             ShortGameDto game, LocalDateTime lastUpdate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.seller = seller;
        this.game = game;
        this.lastUpdate = lastUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ShortSellerProfileReadDto getSeller() {
        return seller;
    }

    public void setSeller(ShortSellerProfileReadDto seller) {
        this.seller = seller;
    }

    public ShortGameDto getGame() {
        return game;
    }

    public void setGame(ShortGameDto game) {
        this.game = game;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
