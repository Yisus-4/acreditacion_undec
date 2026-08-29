package com.undec.acreditacion.infrastructure.persistence;

import com.undec.acreditacion.application.output.PasswordHasher;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@link UserRepository} adapter.
 *
 * <p><strong>SAFE LIMIT - NOT PRODUCTION PERSISTENCE.</strong> JPA / Flyway /
 * PostgreSQL integration is intentionally NOT included in this iteration: there
 * is no database configuration, credentials, or schema migration in the project
 * yet, so wiring JPA would only produce a non-executable app. This adapter keeps
 * the endpoint fully functional end-to-end (real BCrypt verification, real HTTP)
 * while persistence to a relational store is reported as a pending, separately
 * decidable work item. State is lost on restart.</p>
 *
 * <p>It seeds a single demo user so the first real login can be exercised. The
 * demo credentials are NEVER hardcoded in source: they are read from the dev
 * configuration ({@code acreditacion.security.demo.email} /
 * {@code acreditacion.security.demo.password}), which is documented as dev-only
 * and overridable through environment variables. The raw password is hashed
 * through {@link PasswordHasher} immediately and only the resulting hash is kept
 * in the {@link User} entity; the raw value is never stored nor logged.</p>
 */
@Component
public final class InMemoryUserRepositoryAdapter implements UserRepository {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepositoryAdapter(PasswordHasher passwordHasher,
                                         @Value("${acreditacion.security.demo.email}") String demoEmail,
                                         @Value("${acreditacion.security.demo.password}") String demoPassword) {
        Objects.requireNonNull(passwordHasher, "Password hasher is required");
        Objects.requireNonNull(demoEmail, "Demo email is required (set acreditacion.security.demo.email)");
        Objects.requireNonNull(demoPassword, "Demo password is required (set acreditacion.security.demo.password)");

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
