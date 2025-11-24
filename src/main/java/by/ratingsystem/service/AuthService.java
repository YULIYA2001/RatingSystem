package by.ratingsystem.service;

import by.ratingsystem.dto.AuthRequestDto;
import by.ratingsystem.dto.CheckCodeDto;
import by.ratingsystem.dto.JwtAuthenticationDto;
import by.ratingsystem.dto.RefreshTokenDto;
import by.ratingsystem.dto.UserCraeteDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.dto.VerifyUserDto;

import javax.naming.AuthenticationException;

public interface AuthService {
    UserReadDto register(UserCraeteDto userDto);
    JwtAuthenticationDto singIn(AuthRequestDto authDto) throws AuthenticationException;
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;
    Long verifyUser(VerifyUserDto input);
    void resendVerificationCode(String email);
    void changePassword(Long userId, String newPassword);
    boolean isCodeVaild(CheckCodeDto checkCodeDto);
}
