package com.undec.acreditacion.infrastructure.user.dto;

import java.util.List;

public record UpdateUserRequest(
        String username,
        String email,
        List<String> roleCodes,
        String newPassword
) {
}
