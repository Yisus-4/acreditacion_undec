package com.undec.acreditacion.application.output;

import com.undec.acreditacion.domain.entities.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Optional<Role> findById(UUID roleId);

    Optional<Role> findByCode(String code);

    Role save(Role role);
}
