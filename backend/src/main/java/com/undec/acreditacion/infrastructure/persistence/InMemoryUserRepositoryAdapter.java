package com.undec.acreditacion.infrastructure.persistence;

import com.undec.acreditacion.application.output.PasswordHasher;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("test")
public final class InMemoryUserRepositoryAdapter implements UserRepository {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepositoryAdapter(PasswordHasher passwordHasher) {
        this(passwordHasher, "demo@undec.edu.ar", "demo123");
    }

    public InMemoryUserRepositoryAdapter(PasswordHasher passwordHasher, String demoEmail, String demoPassword) {
        Objects.requireNonNull(passwordHasher, "Password hasher is required");
        Objects.requireNonNull(demoEmail, "Demo email is required");
        Objects.requireNonNull(demoPassword, "Demo password is required");

        User demo = User.register("admin", demoEmail, passwordHasher.hash(demoPassword));
        users.put(demo.getId(), demo);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
