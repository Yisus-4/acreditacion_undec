package com.undec.acreditacion.infrastructure.auth;

import com.undec.acreditacion.application.exception.AuthenticationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Translates application-layer authentication failures and malformed requests
 * into uniform, generic HTTP responses so the wire body never reveals which
 * check failed (unknown user, wrong password, inactive user, invalid input).
 */
@RestControllerAdvice
public final class AuthExceptionHandler {

    private static final String GENERIC_AUTH_ERROR = AuthenticationFailedException.GENERIC_MESSAGE;
    private static final String GENERIC_REQUEST_ERROR = "Invalid request";

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationFailure(AuthenticationFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", GENERIC_AUTH_ERROR));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMalformedRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", GENERIC_REQUEST_ERROR));
    }
}
