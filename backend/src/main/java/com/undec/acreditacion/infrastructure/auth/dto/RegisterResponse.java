package com.undec.acreditacion.infrastructure.auth.dto;

import java.util.List;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String username,
        String email,
        boolean active,
        List<String> roleCodes
) {
}
