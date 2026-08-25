package com.undec.acreditacion.domain.entities;

import com.undec.acreditacion.domain.exceptions.DomainValidationException;

import java.util.Objects;
import java.util.UUID;

public final class Permission {

    private final UUID id;
    private final String code;
    private final String name;
    private final String description;

    public Permission(UUID id, String code, String name, String description) {
        this.id = requireId(id);
        this.code = requireCode(code, "permission");
        this.name = requireText(name, "Permission name");
        this.description = requireText(description, "Permission description");
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Permission permission)) {
            return false;
        }
        return id.equals(permission.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private static UUID requireId(UUID value) {
        return Objects.requireNonNull(value, "Permission id is required");
    }

    static String requireCode(String value, String type) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(type + " code is required");
        }
        String normalized = value.trim().toUpperCase();
        if (!normalized.matches("[A-Z0-9]+(?:_[A-Z0-9]+)*")) {
            throw new DomainValidationException(type + " code must use stable uppercase segments separated by underscores");
        }
        return normalized;
    }

    static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(field + " is required");
        }
        return value.trim();
    }
}
