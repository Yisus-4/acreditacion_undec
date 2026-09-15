package com.undec.acreditacion.application.output;

import com.undec.acreditacion.domain.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID userId);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    User save(User user);
}
