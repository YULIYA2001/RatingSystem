package by.ratingsystem.dto;

import java.time.LocalDateTime;

public class SellerProfileReadDto extends ShortSellerProfileReadDto {
    private UserReadDto user;
    private String description;
    private LocalDateTime createdDate;
    private String status;
    private String rating;

    public SellerProfileReadDto(Long id, String nickname, UserReadDto user, String description,
                                LocalDateTime createdDate, String status, String rating) {
        super(id, nickname);
        this.user = user;
        this.description = description;
        this.createdDate = createdDate;
        this.status = status;
        this.rating = rating;
    }

    public UserReadDto getUser() {
        return user;
    }

    public void setUser(UserReadDto user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
