package by.ratingsystem.service;

import by.ratingsystem.dto.AuthRequestDto;
import by.ratingsystem.dto.CheckCodeDto;
import by.ratingsystem.dto.JwtResponseDto;
import by.ratingsystem.dto.RefreshTokenDto;
import by.ratingsystem.dto.UserCreateDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.dto.VerifyUserDto;
import by.ratingsystem.exception.JwtAuthenticationException;
import by.ratingsystem.exception.VerificationCodeException;
import by.ratingsystem.model.User;
import by.ratingsystem.model.enums.Role;
import by.ratingsystem.repository.UserRepository;
import by.ratingsystem.security.jwt.JwtService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private static final String verifyHtmlMessage = """
        <html>
            <body style="font-family: Arial, sans-serif;">
                <div style="background-color: #f5f5f5; padding: 20px;">
                    <h2 style="color: #333;">Welcome to RatingSystemApp!</h2>
                    <div style="background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                        <h3 style="color: #333;">Verification Code:</h3>
                        <p style="font-size: 18px; font-weight: bold; color: #007bff;">
                            %s
                        </p>
                    </div>
                </div>
            </body>
        </html>
        """;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationService verificationService;

    public AuthService(UserRepository userRepository, JwtService jwtService,
                       PasswordEncoder passwordEncoder, EmailService emailService,
                       VerificationService verificationService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationService = verificationService;
    }

    public UserReadDto register(UserCreateDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setRole(Role.SELLER);
        user.setVerified(false);

        String code = verificationService.generateAndStoreCode(userDto.getEmail());
        sendVerificationEmail(user.getEmail(), code);

        return mapToReadDto(userRepository.save(user));
    }

    private UserReadDto mapToReadDto(User user) {
        return new UserReadDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.isVerified()
        );
    }

    public JwtResponseDto singIn(AuthRequestDto authDto) {
        User user = userRepository.findByEmail(authDto.getEmail()).orElseThrow(() -> new JwtAuthenticationException("Email or password is not correct"));;
        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new JwtAuthenticationException("Email or password is not correct");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        return jwtService.generateAuthToken(user.getEmail());
    }

    public JwtResponseDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new JwtAuthenticationException("Invalid refresh token");
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public Long verifyUser(VerifyUserDto verifyUserDto) {
        boolean isVerified =  verificationService.verifyCode(verifyUserDto.getEmail(), verifyUserDto.getVerificationCode());

        User user = findByEmail(verifyUserDto.getEmail());

        if (!isVerified) {
            throw new VerificationCodeException("Invalid or expired verification code");
        } else {
            verificationService.deleteUsedCode(verifyUserDto.getEmail());

            user.setVerified(true);
            userRepository.save(user);
            return user.getId();
        }
    }

    public boolean isCodeVaild(CheckCodeDto checkCodeDto) {
        return verificationService.verifyCode(checkCodeDto.getEmail(), checkCodeDto.getCode());
    }

    public void resendVerificationCode(String email) {
        String code = verificationService.generateAndStoreCode(email);
        this.sendVerificationEmail(email, code);
    }

    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    private void sendVerificationEmail(String email, String code) {
        String subject = "Account Verification";
        String htmlMessage = verifyHtmlMessage.formatted(code);

        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}