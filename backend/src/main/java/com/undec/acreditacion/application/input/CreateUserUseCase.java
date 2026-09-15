package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.User;

public interface CreateUserUseCase {

    User createUser(CreateUserCommand command);
}
