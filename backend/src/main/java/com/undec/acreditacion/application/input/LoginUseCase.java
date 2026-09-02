package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.User;

public interface LoginUseCase {

    User login(String email, String rawPassword);
}
