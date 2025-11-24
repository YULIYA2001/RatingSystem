package by.ratingsystem.dto.seller;

import by.ratingsystem.dto.user.UserReadDto;

import java.time.LocalDateTime;

public class SellerProfileAdminReadDto extends SellerProfileReadDto {
    private UserReadDto user;
    private String status;

    public SellerProfileAdminReadDto(Long id, String nickname, Long userId, String description, LocalDateTime createdDate,
                                     String avgRating, int reviewsCount, UserReadDto user, String status) {
        super(id, nickname, userId, description, createdDate, avgRating, reviewsCount);
        this.user = user;
        this.status = status;
    }

    public UserReadDto getUser() {
        return user;
    }

    public void setUser(UserReadDto user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
