package com.undec.acreditacion.infrastructure.persistence.postgres;

import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("!test")
public class PostgresUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserPersistenceMapper mapper;

    public PostgresUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository,
                                         UserPersistenceMapper mapper) {
        this.springDataUserRepository = Objects.requireNonNull(springDataUserRepository, "SpringDataUserRepository is required");
        this.mapper = Objects.requireNonNull(mapper, "UserPersistenceMapper is required");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID userId) {
        return springDataUserRepository.findById(userId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return springDataUserRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return springDataUserRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved = springDataUserRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
