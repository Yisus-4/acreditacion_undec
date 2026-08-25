package com.undec.acreditacion.application.output;

import com.undec.acreditacion.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID userId);

    User save(User user);
}
