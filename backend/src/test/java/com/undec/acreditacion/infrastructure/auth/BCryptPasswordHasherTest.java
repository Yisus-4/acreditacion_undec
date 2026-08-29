package com.undec.acreditacion.infrastructure.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the BCrypt adapter never stores or compares plaintext: hashes are
 * not the raw value, do not contain it, and verification goes through the
 * encoder rather than string equality.
 */
final class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    @DisplayName("hash is never the plaintext and does not embed it")
    void hashDoesNotStorePlaintext() {
        String raw = "Admin123!";

        String hashed = hasher.hash(raw);

        assertNotEquals(raw, hashed, "hash must not equal the raw password");
        assertFalse(hashed.contains(raw), "hash must not contain the raw password");
        assertTrue(hashed.startsWith("$2"), "hash must be a BCrypt-format string");
    }

    @Test
    @DisplayName("matches verifies through the encoder, not plaintext equality")
    void matchesVerifiesWithoutPlaintextComparison() {
        String hashed = hasher.hash("Admin123!");

        assertTrue(hasher.matches("Admin123!", hashed), "correct password must verify");
        assertFalse(hasher.matches("wrong", hashed), "wrong password must not verify");
        assertFalse(hasher.matches("Admin123!", "not-a-bcrypt-hash"), "malformed hash must not verify");
    }

    @Test
    @DisplayName("same password yields different hashes (per-call salt) and both verify")
    void hashProducesSaltedVariations() {
        String first = hasher.hash("Admin123!");
        String second = hasher.hash("Admin123!");

        assertNotEquals(first, second, "salt must produce different hashes for the same input");
        assertTrue(hasher.matches("Admin123!", first));
        assertTrue(hasher.matches("Admin123!", second));
    }
}
