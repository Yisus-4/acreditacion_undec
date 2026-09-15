package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.ChangeUserStatusUseCase;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;

import java.util.Objects;
import java.util.UUID;

public final class ChangeUserStatusService implements ChangeUserStatusUseCase {

    private final UserRepository userRepository;

    public ChangeUserStatusService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository is required");
    }

    @Override
    public User changeStatus(UUID userId, boolean active) {
        Objects.requireNonNull(userId, "User id is required");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (active) {
            user.activate();
        } else {
            user.deactivate();
        }

        return userRepository.save(user);
    }
}
