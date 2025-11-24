package by.ratingsystem.dto.auth;

public class RefreshTokenDto {

    private String refreshToken;

    public RefreshTokenDto(){}

    public RefreshTokenDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
