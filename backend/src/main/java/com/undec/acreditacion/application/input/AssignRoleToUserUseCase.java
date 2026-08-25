package com.undec.acreditacion.application.input;

import java.util.UUID;

public interface AssignRoleToUserUseCase {

    void assignRole(UUID userId, UUID roleId);
}
