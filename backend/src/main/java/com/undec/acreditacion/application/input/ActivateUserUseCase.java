package com.undec.acreditacion.application.input;

import java.util.UUID;

public interface ActivateUserUseCase {

    void activate(UUID userId);
}
