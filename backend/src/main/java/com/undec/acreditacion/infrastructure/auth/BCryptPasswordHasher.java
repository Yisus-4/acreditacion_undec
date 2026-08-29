package com.undec.acreditacion.infrastructure.auth;

import com.undec.acreditacion.application.output.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Concrete {@link PasswordHasher} adapter built on Spring Security's
 * {@link BCryptPasswordEncoder}. This is the only place in the codebase that
 * knows about BCrypt; the application layer remains agnostic.
 */
@Component
public final class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
