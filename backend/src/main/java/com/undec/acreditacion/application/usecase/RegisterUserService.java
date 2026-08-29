package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.RegisterUserUseCase;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;

import java.util.Objects;

public final class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;

    public RegisterUserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository is required");
    }

    @Override
    public User register(String username, String email, String passwordHash) {
        User user = User.register(username, email, passwordHash);

        userRepository.findByEmail(email).ifPresent(existingUser -> {
            throw new IllegalArgumentException("User already exists with email: " + email);
        });

        return userRepository.save(user);
    }
}
