package com.undec.acreditacion.domain.auth;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record TokenPayload(
        UUID userId,
        String email,
        List<String> roles,
        Instant expiresAt
) {
    public TokenPayload {
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(email, "email is required");
        Objects.requireNonNull(expiresAt, "expiresAt is required");
        roles = roles != null ? List.copyOf(roles) : Collections.emptyList();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
