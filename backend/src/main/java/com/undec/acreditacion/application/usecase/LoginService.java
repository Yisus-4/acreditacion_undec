package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.exception.AuthenticationFailedException;
import com.undec.acreditacion.application.input.LoginUseCase;
import com.undec.acreditacion.application.output.PasswordHasher;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;

import java.util.Objects;

/**
 * Login use case. Orchestrates: lookup by email, active check, password
 * verification through the {@link PasswordHasher} port. Every failure path
 * raises the same generic {@link AuthenticationFailedException}.
 */
public final class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository is required");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "Password hasher is required");
    }

    @Override
    public User login(String email, String rawPassword) {
        if (isBlank(email) || isBlank(rawPassword)) {
            throw new AuthenticationFailedException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(AuthenticationFailedException::new);

        if (!user.isActive()) {
            throw new AuthenticationFailedException();
        }

        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new AuthenticationFailedException();
        }

        return user;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
