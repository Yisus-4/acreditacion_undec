package com.undec.acreditacion.application.input;

import java.util.List;
import java.util.UUID;

public record UpdateUserCommand(
        UUID userId,
        String username,
        String email,
        List<String> roleCodes,
        String optionalNewPassword
) {
}
