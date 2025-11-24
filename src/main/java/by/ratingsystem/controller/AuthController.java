package by.ratingsystem.controller;

import by.ratingsystem.dto.AuthRequestDto;
import by.ratingsystem.dto.CheckCodeDto;
import by.ratingsystem.dto.JwtAuthenticationDto;
import by.ratingsystem.dto.RefreshTokenDto;
import by.ratingsystem.dto.ResetPasswordDto;
import by.ratingsystem.dto.UserCraeteDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.dto.VerifyUserDto;
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
    public ResponseEntity<UserReadDto> register(@RequestBody UserCraeteDto userDto) {
        return new ResponseEntity<>(authService.register(userDto), HttpStatus.CREATED);
    }

    @PostMapping({"/verify"})
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            this.authService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationDto> singIn(@RequestBody AuthRequestDto authDto) {
        try {
            JwtAuthenticationDto jwtAuthenticationDto = authService.singIn(authDto);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (javax.naming.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh-token")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return authService.refreshToken(refreshTokenDto);
    }

    @PostMapping({"/forgot-password"})
    public ResponseEntity<?> resendVerificationCodeForNewPassword(@RequestBody String email) {
        try {
            this.authService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping({"/reset"})
    public ResponseEntity<HttpStatus> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        Long userId = authService.verifyUser(new VerifyUserDto(resetPasswordDto.getEmail(), resetPasswordDto.getCode()));
        if (userId != null) {
            authService.changePassword(userId, resetPasswordDto.getNewPassword());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping({"/check-code"})
    public ResponseEntity<String> checkCode(@RequestBody CheckCodeDto checkCodeDto) {
        String msg = "Your code is " + (authService.isCodeVaild(checkCodeDto) ? "valid" : "invalid");
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
}
