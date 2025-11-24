package by.ratingsystem.service.impl;

import by.ratingsystem.dto.AuthRequestDto;
import by.ratingsystem.dto.CheckCodeDto;
import by.ratingsystem.dto.JwtResponseDto;
import by.ratingsystem.dto.RefreshTokenDto;
import by.ratingsystem.dto.UserCraeteDto;
import by.ratingsystem.dto.UserReadDto;
import by.ratingsystem.dto.VerifyUserDto;
import by.ratingsystem.exception.JwtAuthenticationException;
import by.ratingsystem.exception.VerificationCodeException;
import by.ratingsystem.model.Role;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.UserRepository;
import by.ratingsystem.security.jwt.JwtService;
import by.ratingsystem.service.AuthService;
import by.ratingsystem.service.EmailService;
import by.ratingsystem.service.VerificationService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
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

    public AuthServiceImpl(UserRepository userRepository, JwtService jwtService,
                           PasswordEncoder passwordEncoder, EmailService emailService,
                           VerificationService verificationService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationService = verificationService;
    }

    @Override
    public UserReadDto register(UserCraeteDto userDto) {
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

    @Override
    public JwtResponseDto singIn(AuthRequestDto authDto) throws AuthenticationException {
        User user = userRepository.findByEmail(authDto.getEmail()).orElseThrow(() -> new AuthenticationException("Email or password is not correct"));;
        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Email or password is not correct");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
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

    @Override
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

    @Override
    public boolean isCodeVaild(CheckCodeDto checkCodeDto) {
        return verificationService.verifyCode(checkCodeDto.getEmail(), checkCodeDto.getCode());
    }

    @Override
    public void resendVerificationCode(String email) {
        String code = verificationService.generateAndStoreCode(email);
        this.sendVerificationEmail(email, code);
    }

    @Override
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