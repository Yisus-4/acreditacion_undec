package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.User;

import java.util.UUID;

public interface ChangeUserStatusUseCase {

    User changeStatus(UUID userId, boolean active);
}
