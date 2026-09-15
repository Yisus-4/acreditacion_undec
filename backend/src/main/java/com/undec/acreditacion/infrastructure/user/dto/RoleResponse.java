package com.undec.acreditacion.infrastructure.user.dto;

import com.undec.acreditacion.domain.entities.Role;

import java.util.UUID;

public record RoleResponse(
        UUID id,
        String code,
        String name,
        String description
) {
    public static RoleResponse fromDomain(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription()
        );
    }
}
