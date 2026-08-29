package com.undec.acreditacion.infrastructure.auth.dto;

import java.util.UUID;

/**
 * <strong>Development-only login response.</strong>
 *
 * <p>This contract is deliberately separated from the future authenticated
 * response: it does NOT carry a token, session, or any claim that could be
 * mistaken for completed authentication. The {@code mode} field is set to
 * {@code "DEMO"} so clients can detect that they are talking to a non-productive
 * slice. When JWT is introduced, a distinct authenticated response (with token
 * and claims) will replace this contract; that decision is intentionally out of
 * scope for this iteration.</p>
 */
public record LoginResponse(UUID id, String username, String email, boolean active, String mode) {

    public static final String DEMO_MODE = "DEMO";

    public static LoginResponse demo(UserView user) {
        return new LoginResponse(user.id(), user.username(), user.email(), user.active(), DEMO_MODE);
    }

    /** Minimal read-only projection of a {@code User} to avoid leaking domain internals. */
    public record UserView(UUID id, String username, String email, boolean active) {
    }
}
