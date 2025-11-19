package by.ratingsystem.dto;

public class GameObjectCreateDto {
    private String title;
    private String description;
    private GameDto game;

    public GameObjectCreateDto(String title, String description, GameDto game) {
        this.title = title;
        this.description = description;
        this.game = game;
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

    public GameDto getGame() {
        return game;
    }

    public void setGame(GameDto game) {
        this.game = game;
    }
}
