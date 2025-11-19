package by.ratingsystem.dto;

public class SellerProfileCreateDto {
    private String nickname;
    private String description;

    public SellerProfileCreateDto(String nickname, String description) {
        this.nickname = nickname;
        this.description = description;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
