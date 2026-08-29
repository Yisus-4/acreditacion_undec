package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.ActivateUserUseCase;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;

import java.util.Objects;
import java.util.UUID;

public final class ActivateUserService implements ActivateUserUseCase {

    private final UserRepository userRepository;

    public ActivateUserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository es requerido");
    }

    @Override
    public void activate(UUID userId) {
        User user = findUser(userId);
        user.activate();
        userRepository.save(user);
    }

    private User findUser(UUID userId) {
        Objects.requireNonNull(userId, "User id es requerido");
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));
    }
}
