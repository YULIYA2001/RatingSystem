package by.ratingsystem.dto;

public class VerifyUserDto {
    private String email;
    private String verificationCode;

    public VerifyUserDto(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }

    public String getEmail() {
        return this.email;
    }

    public String getVerificationCode() {
        return this.verificationCode;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setVerificationCode(final String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
