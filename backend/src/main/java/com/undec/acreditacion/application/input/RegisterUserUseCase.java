package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.User;

public interface RegisterUserUseCase {

    User register(String username, String email, String passwordHash);
}
