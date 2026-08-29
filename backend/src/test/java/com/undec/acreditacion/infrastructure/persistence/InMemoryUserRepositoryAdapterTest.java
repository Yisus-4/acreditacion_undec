package com.undec.acreditacion.infrastructure.persistence;

import com.undec.acreditacion.domain.entities.User;
import com.undec.acreditacion.infrastructure.auth.BCryptPasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that the in-memory adapter seeds a demo user from externalized dev
 * configuration and that the raw password is never stored in the entity: only
 * the BCrypt hash is kept, and verification goes through the hasher.
 */
final class InMemoryUserRepositoryAdapterTest {

    private static final String DEMO_EMAIL = "admin@undec.edu";
    private static final String DEMO_PASSWORD = "admin123";

    @Test
    @DisplayName("seeds a demo user whose stored hash is not the raw password")
    void seedsDemoUserWithoutStoringPlaintext() {
        BCryptPasswordHasher hasher = new BCryptPasswordHasher();
        InMemoryUserRepositoryAdapter adapter = new InMemoryUserRepositoryAdapter(
                hasher, DEMO_EMAIL, DEMO_PASSWORD);

        Optional<User> seeded = adapter.findByEmail(DEMO_EMAIL);

        assertTrue(seeded.isPresent(), "demo user must be seeded by email");
        User user = seeded.get();
        assertEquals(DEMO_EMAIL, user.getEmail());
        assertTrue(user.isActive());

        String storedHash = user.getPasswordHash();
        assertNotEquals(DEMO_PASSWORD, storedHash, "entity must not store the raw password");
        assertFalse(storedHash.contains(DEMO_PASSWORD),
                "stored hash must not contain the raw password");
        assertTrue(storedHash.startsWith("$2"), "stored hash must be a BCrypt-format string");
        assertTrue(hasher.matches(DEMO_PASSWORD, storedHash),
                "the real demo password must verify against the stored hash");
        assertFalse(hasher.matches("wrong", storedHash),
                "a wrong password must not verify");
    }
}
