package by.ratingsystem.exception.handler;

import by.ratingsystem.exception.DuplicateEntityException;
import by.ratingsystem.exception.JwtAuthenticationException;
import by.ratingsystem.exception.VerificationCodeException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    private static final Logger LOGGER = LogManager.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalStateException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleException(DuplicateEntityException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleException(JwtAuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(VerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleException(VerificationCodeException ex) {
        return buildResponse(ex.getHttpStatus(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        LOGGER.error(ex.getMessage());
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error has occurred. Please contact the administrator."
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Exception ex) {
        return buildResponse(status, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String expMessage) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        expMessage
                ));
    }
}
