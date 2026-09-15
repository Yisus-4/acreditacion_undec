package com.undec.acreditacion.infrastructure.auth.dto;

import java.util.List;

public record RegisterRequest(
        String username,
        String email,
        String password,
        List<String> roleCodes
) {
}
