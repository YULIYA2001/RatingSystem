package by.ratingsystem.service.impl;

import by.ratingsystem.exception.VerificationCodeException;
import by.ratingsystem.service.VerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationServiceImpl implements VerificationService {
    @Value("${verification.code.timeout}")
    private long codeTimeout;

    private final StringRedisTemplate redisTemplate;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public VerificationServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateAndStoreCode(String email) {
        try {
            String key = "verify:" + email;
            String code = generateVerificationCode();

            redisTemplate.opsForValue().set(key, code, codeTimeout, TimeUnit.HOURS);

            return code;
        } catch (Exception e) {
            throw new VerificationCodeException(
                    "Verification code generation failed. Please contact the administrator.", e);
        }
    }

    @Override
    public boolean verifyCode(String email, String codeInput) {
        String key = "verify:" + email;
        String codeStored = redisTemplate.opsForValue().get(key);

        return codeStored != null && codeStored.equals(codeInput);
    }

    @Override
    public void deleteUsedCode(String email) {
        String key = "verify:" + email;
        redisTemplate.delete(key);
    }

    private String generateVerificationCode() {
        return String.valueOf(SECURE_RANDOM.nextInt(900000) + 100000);
    }
}
