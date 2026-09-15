package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListUsersServiceTest {

    @Test
    void returnsAllUsersFromRepository() {
        UserRepository userRepository = mock(UserRepository.class);
        ListUsersService service = new ListUsersService(userRepository);

        User u1 = User.rehydrate(UUID.randomUUID(), "u1", "u1@undec.edu.ar", "h1", true, false, Set.of());
        User u2 = User.rehydrate(UUID.randomUUID(), "u2", "u2@undec.edu.ar", "h2", true, false, Set.of());
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> result = service.listUsers();
        assertEquals(2, result.size());
    }
}
