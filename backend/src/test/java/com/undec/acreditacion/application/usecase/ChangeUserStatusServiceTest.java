package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;
import com.undec.acreditacion.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChangeUserStatusServiceTest {

    private UserRepository userRepository;
    private ChangeUserStatusService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new ChangeUserStatusService(userRepository);
    }

    @Test
    @DisplayName("Activates user successfully")
    void activatesUser() {
        UUID userId = UUID.randomUUID();
        User user = User.rehydrate(userId, "user1", "user1@undec.edu.ar", "hash", false, false, Set.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = service.changeStatus(userId, true);

        assertTrue(updated.isActive());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deactivates regular user successfully")
    void deactivatesRegularUser() {
        UUID userId = UUID.randomUUID();
        User user = User.rehydrate(userId, "user1", "user1@undec.edu.ar", "hash", true, false, Set.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = service.changeStatus(userId, false);

        assertFalse(updated.isActive());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deactivating system user throws DomainValidationException")
    void deactivatingSystemUserThrowsDomainException() {
        UUID userId = UUID.randomUUID();
        User systemUser = User.rehydrate(userId, "admin", "admin@undec.edu.ar", "hash", true, true, Set.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(systemUser));

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> service.changeStatus(userId, false));
        assertEquals("System user cannot be deactivated", ex.getMessage());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when user not found")
    void throwsWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.changeStatus(userId, true));
    }
}
