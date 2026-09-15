package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.User;

public interface UpdateUserUseCase {

    User updateUser(UpdateUserCommand command);
}
