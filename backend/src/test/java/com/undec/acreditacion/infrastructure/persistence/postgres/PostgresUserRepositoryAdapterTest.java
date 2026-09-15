package com.undec.acreditacion.infrastructure.persistence.postgres;

import com.undec.acreditacion.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresUserRepositoryAdapterTest {

    private SpringDataUserRepository springDataUserRepository;
    private UserPersistenceMapper mapper;
    private PostgresUserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        springDataUserRepository = mock(SpringDataUserRepository.class);
        mapper = mock(UserPersistenceMapper.class);
        adapter = new PostgresUserRepositoryAdapter(springDataUserRepository, mapper);
    }

    @Test
    @DisplayName("findByEmail delegates to springDataUserRepository and maps to domain")
    void findByEmailDelegatesAndMaps() {
        String email = "admin@undec.edu.ar";
        UserJpaEntity entity = new UserJpaEntity();
        User domain = User.rehydrate(UUID.randomUUID(), "admin", email, "hash", true, Set.of());

        when(springDataUserRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<User> result = adapter.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(springDataUserRepository).findByEmail(email);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("save delegates to mapper and springDataUserRepository")
    void saveDelegates() {
        User domain = User.rehydrate(UUID.randomUUID(), "admin", "admin@undec.edu.ar", "hash", true, Set.of());
        UserJpaEntity entity = new UserJpaEntity();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(springDataUserRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        User saved = adapter.save(domain);

        assertEquals(domain, saved);
        verify(mapper).toEntity(domain);
        verify(springDataUserRepository).save(entity);
        verify(mapper).toDomain(entity);
    }
}
