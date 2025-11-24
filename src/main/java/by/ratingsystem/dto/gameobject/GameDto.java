package by.ratingsystem.dto.gameobject;

public class GameDto extends ShortGameDto {
    private String description;
    
    public GameDto(Long id, String name, String description) {
        super(id, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
