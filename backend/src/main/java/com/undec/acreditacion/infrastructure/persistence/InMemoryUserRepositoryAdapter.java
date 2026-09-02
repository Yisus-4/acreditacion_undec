package com.undec.acreditacion.infrastructure.persistence;

import com.undec.acreditacion.application.output.PasswordHasher;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@link UserRepository} adapter.
 *
 * <p><strong>SAFE LIMIT - NOT PRODUCTION PERSISTENCE.</strong> This adapter keeps
 * the endpoint fully functional end-to-end (real BCrypt verification, real HTTP)
 * while persistence to a relational store is reported as a pending work item.
 * State is lost on restart.</p>
 *
 * <p>It seeds a single demo user so the first real login can be exercised. The
 * demo credentials are hardcoded for development purposes and will be replaced
 * by database-backed persistence in the next iteration. The raw password is
 * hashed through {@link PasswordHasher} immediately and only the resulting hash
 * is kept in the {@link User} entity; the raw value is never stored nor logged.</p>
 */
@Component
public final class InMemoryUserRepositoryAdapter implements UserRepository {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepositoryAdapter(PasswordHasher passwordHasher) {
        Objects.requireNonNull(passwordHasher, "Password hasher is required");

        User demo = User.register("admin", "demo@undec.edu.ar", passwordHasher.hash("demo123"));
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
