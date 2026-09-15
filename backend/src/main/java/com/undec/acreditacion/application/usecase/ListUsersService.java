package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.ListUsersUseCase;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;

import java.util.List;
import java.util.Objects;

public final class ListUsersService implements ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository is required");
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
