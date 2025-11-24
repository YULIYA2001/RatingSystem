package by.ratingsystem.service;

public interface VerificationService {
    String generateAndStoreCode(String email);
    boolean verifyCode(String email, String codeInput);
    void deleteUsedCode(String email);
}
