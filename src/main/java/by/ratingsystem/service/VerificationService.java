package by.ratingsystem.service;

import by.ratingsystem.exception.VerificationCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationService {
    @Value("${verification.code.timeout}")
    private long codeTimeout;

    private final StringRedisTemplate redisTemplate;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Autowired
    public VerificationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreCode(String email) {
        try {
            String key = buildKey(email);
            String code = generateVerificationCode();

            redisTemplate.opsForValue().set(key, code, codeTimeout, TimeUnit.HOURS);

            return code;
        } catch (Exception e) {
            throw new VerificationCodeException(
                    "Verification code generation failed. Please contact the administrator.", e);
        }
    }

    public boolean verifyCode(String email, String codeInput) {
        String key = buildKey(email);
        String codeStored = redisTemplate.opsForValue().get(key);

        return codeStored != null && codeStored.equals(codeInput);
    }

    public void deleteUsedCode(String email) {
        String key = buildKey(email);
        redisTemplate.delete(key);
    }

    private String buildKey(String email) {
        return "verify:" + email;
    }

    private String generateVerificationCode() {
        return String.valueOf(SECURE_RANDOM.nextInt(900000) + 100000);
    }
}
