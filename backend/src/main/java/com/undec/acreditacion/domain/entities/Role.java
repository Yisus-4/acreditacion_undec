package com.undec.acreditacion.domain.entities;

import com.undec.acreditacion.domain.exceptions.DomainValidationException;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class Role {

    private final UUID id;
    private final String code;
    private final String name;
    private final String description;
    private final Set<Permission> permissions = new LinkedHashSet<>();
    private boolean active;
    private final boolean systemRole;

    public static Role system(UUID id, String code, String name, String description) {
        return new Role(id, code, name, description, true, true);
    }

    public static Role custom(UUID id, String code, String name, String description) {
        return new Role(id, code, name, description, true, false);
    }

    private Role(UUID id, String code, String name, String description, boolean active, boolean systemRole) {
        this.id = Objects.requireNonNull(id, "Role id is required");
        this.code = Permission.requireCode(code, "role");
        this.name = Permission.requireText(name, "Role name");
        this.description = Permission.requireText(description, "Role description");
        this.active = active;
        this.systemRole = systemRole;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isSystemRole() {
        return systemRole;
    }

    public void deactivate() {
        if (systemRole) {
            throw new DomainValidationException("System role cannot be deactivated");
        }
        active = false;
    }

    public void activate() {
        active = true;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Role role)) {
            return false;
        }
        return id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(Permission permission) {
        if (permission == null) {
            throw new DomainValidationException("Permission is required");
        }
        if (!permissions.add(permission)) {
            throw new DomainValidationException("Permission is already assigned to role");
        }
    }

    public void removePermission(Permission permission) {
        if (!permissions.remove(permission)) {
            throw new DomainValidationException("Permission is not assigned to role");
        }
    }
}
