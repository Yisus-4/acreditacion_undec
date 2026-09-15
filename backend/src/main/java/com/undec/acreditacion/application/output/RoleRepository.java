package com.undec.acreditacion.application.output;

import com.undec.acreditacion.domain.entities.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Optional<Role> findById(UUID roleId);

    Optional<Role> findByCode(String code);

    List<Role> findAll();

    Role save(Role role);
}
