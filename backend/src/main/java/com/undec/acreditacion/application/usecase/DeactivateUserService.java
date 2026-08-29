package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.DeactivateUserUseCase;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;

import java.util.Objects;
import java.util.UUID;

public final class DeactivateUserService implements DeactivateUserUseCase {

    private final UserRepository userRepository;

    public DeactivateUserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository is required");
    }

    @Override
    public void deactivate(UUID userId) {
        User user = findUser(userId);
        user.deactivate();
        userRepository.save(user);
    }

    private User findUser(UUID userId) {
        Objects.requireNonNull(userId, "User id is required");
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
