package com.undec.acreditacion.domain.entities;

import com.undec.acreditacion.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PermissionTest {

    @Test
    void normalizesStableCodeAndRejectsInvalidCode() {
        Permission permission = new Permission(UUID.randomUUID(), "  manage_users ", "Manage users", "Manage users");

        assertEquals("MANAGE_USERS", permission.getCode());
        assertThrows(DomainValidationException.class,
                () -> new Permission(UUID.randomUUID(), "manage-users", "Manage users", "Manage users"));
    }
}
