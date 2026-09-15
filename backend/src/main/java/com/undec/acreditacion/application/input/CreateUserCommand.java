package com.undec.acreditacion.application.input;

import java.util.List;

public record CreateUserCommand(
        String username,
        String email,
        String password,
        List<String> roleCodes
) {
}
