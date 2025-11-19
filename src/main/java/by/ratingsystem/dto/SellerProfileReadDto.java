package by.ratingsystem.dto;

import java.time.LocalDateTime;

public class SellerProfileReadDto extends ShortSellerProfileReadDto {
    private UserReadDto user;
    private String description;
    private LocalDateTime createdDate;
    private String status;

    public SellerProfileReadDto(Long id, UserReadDto user, String nickname, String description,
                                String status, LocalDateTime createdDate) {
        super(id, nickname);
        this.user = user;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
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
}
