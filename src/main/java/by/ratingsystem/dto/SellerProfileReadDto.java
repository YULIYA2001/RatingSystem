package by.ratingsystem.dto;

import java.time.LocalDateTime;

public class SellerProfileReadDto {
    private Long id;
    private UserReadDto user;
    private String nickname;
    private String description;
    private LocalDateTime createdDate;
    private String status;

    public SellerProfileReadDto(Long id, UserReadDto user, String nickname, String description,
                                String status, LocalDateTime createdDate) {
        this.id = id;
        this.user = user;
        this.nickname = nickname;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserReadDto getUser() {
        return user;
    }

    public void setUser(UserReadDto user) {
        this.user = user;
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
