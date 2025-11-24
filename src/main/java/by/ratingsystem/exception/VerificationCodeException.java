package by.ratingsystem.exception;

import org.springframework.http.HttpStatus;

public class VerificationCodeException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public VerificationCodeException(String message) {
        super(message);
    }

    public VerificationCodeException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
