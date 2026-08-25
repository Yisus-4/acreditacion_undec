package com.undec.acreditacion.application.output;

import com.undec.acreditacion.domain.entities.Permission;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {

    Optional<Permission> findById(UUID permissionId);

    Optional<Permission> findByCode(String code);

    Permission save(Permission permission);
}
