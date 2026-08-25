package com.undec.acreditacion.domain.entities;

import com.undec.acreditacion.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void startsActiveAndAssignsEachRoleOnlyOnce() {
        User user = User.register("user", "user@example.org", "encoded-password");
        Role role = Role.system(UUID.randomUUID(), "ADMIN", "Administrator", "Manages the system");

        user.assignRole(role);

        assertTrue(user.isActive());
        assertEquals(1, user.getRoles().size());
        assertThrows(DomainValidationException.class, () -> user.assignRole(role));
    }

    @Test
    void registerCreatesAnActiveUserWithoutRoles() {
        User user = User.register("user", "user@example.org", "encoded-password");

        assertTrue(user.isActive());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void deactivatedUserCannotReceiveRoles() {
        User user = user();
        user.deactivate();

        assertFalse(user.isActive());
        assertThrows(DomainValidationException.class,
                () -> user.assignRole(Role.custom(UUID.randomUUID(), "EDITOR", "Editor", "Edits content")));
    }

    @Test
    void activationRestoresTheAbilityToReceiveRoles() {
        User user = user();
        user.deactivate();

        user.activate();
        user.assignRole(Role.custom(UUID.randomUUID(), "EDITOR", "Editor", "Edits content"));

        assertTrue(user.isActive());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void validatesEmail() {
        assertThrows(DomainValidationException.class,
                () -> User.register("user", "invalid-email", "hash"));
    }

    @Test
    void rehydratesPersistedStateWithoutForcingUserToBeActive() {
        UUID id = UUID.randomUUID();
        Role role = Role.custom(UUID.randomUUID(), "EDITOR", "Editor", "Edits content");

        User user = User.rehydrate(id, "user", "user@example.org", "encoded-password", false,
                 Set.of(role));

        assertEquals(id, user.getId());
        assertFalse(user.isActive());
        assertEquals(java.util.Set.of(role), user.getRoles());
        assertThrows(DomainValidationException.class, () -> user.assignRole(role));
    }

    @Test
    void rehydrateRequiresRolesToMakePersistedStateExplicit() {
        assertThrows(DomainValidationException.class,
                () -> User.rehydrate(UUID.randomUUID(), "user", "user@example.org", "hash", true, null));
    }

    private static User user() {
        return User.register("user", "user@example.org", "encoded-password");
    }
}
