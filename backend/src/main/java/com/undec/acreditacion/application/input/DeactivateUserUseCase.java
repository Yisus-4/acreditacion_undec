package com.undec.acreditacion.application.input;

import java.util.UUID;

public interface DeactivateUserUseCase {

    void deactivate(UUID userId);
}
