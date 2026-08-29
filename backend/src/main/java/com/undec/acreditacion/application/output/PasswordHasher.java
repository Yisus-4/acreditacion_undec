package com.undec.acreditacion.application.output;

/**
 * Output port for password hashing and verification.
 *
 * <p>The application layer defines the contract; the concrete BCrypt adapter
 * lives in infrastructure. Domain never depends on this port.</p>
 */
public interface PasswordHasher {

    /** Produces a salted hash for a raw password (e.g. for registration). */
    String hash(String rawPassword);

    /**
     * Verifies a raw password against a previously stored hash without ever
     * materialising the raw password outside the call.
     */
    boolean matches(String rawPassword, String hashedPassword);
}
