package by.ratingsystem.controller;

import by.ratingsystem.dto.auth.ResetPasswordDto;
import by.ratingsystem.dto.auth.AuthRequestDto;
import by.ratingsystem.dto.auth.AuthResponseDto;
import by.ratingsystem.dto.auth.CheckVerificationCodeDto;
import by.ratingsystem.dto.auth.RefreshTokenDto;
import by.ratingsystem.dto.user.UserCreateDto;
import by.ratingsystem.dto.user.UserReadDto;
import by.ratingsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registration")
    public ResponseEntity<UserReadDto> register(@RequestBody UserCreateDto userDto) {
        return new ResponseEntity<>(authService.register(userDto), HttpStatus.CREATED);
    }

    @PostMapping({"/verify"})
    public ResponseEntity<String> verifyUser(@RequestBody CheckVerificationCodeDto checkVerificationCodeDto) {
        authService.verifyUser(checkVerificationCodeDto);
        return new ResponseEntity<>("Account verified successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> singIn(@RequestBody AuthRequestDto authDto) {
        AuthResponseDto authResponseDto = authService.singIn(authDto);
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        AuthResponseDto authResponseDto = authService.refreshToken(refreshTokenDto);
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping({"/forgot-password"})
    public ResponseEntity<String> resendVerificationCodeForNewPassword(@RequestBody String email) {
        authService.resendVerificationCode(email);
        return new ResponseEntity<>("Verification code sent to %s".formatted(email), HttpStatus.OK);
    }

    @PostMapping({"/reset"})
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        Long userId = authService.verifyUser(new CheckVerificationCodeDto(
                resetPasswordDto.getEmail(),
                resetPasswordDto.getVerificationCode()
        ));
        if (userId != null) {
            authService.changePassword(userId, resetPasswordDto.getNewPassword());
        }
        return new ResponseEntity<>("Password was changed", HttpStatus.OK);
    }

    @PostMapping({"/check-code"})
    public ResponseEntity<String> checkCode(@RequestBody CheckVerificationCodeDto checkVerificationCodeDto) {
        String msg = "Your code is " + (authService.isCodeValid(checkVerificationCodeDto) ? "valid" : "invalid");
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
}
