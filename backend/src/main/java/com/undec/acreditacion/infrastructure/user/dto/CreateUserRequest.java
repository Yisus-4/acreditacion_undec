package com.undec.acreditacion.infrastructure.user.dto;

import java.util.List;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        List<String> roleCodes
) {
}
