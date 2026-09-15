package com.undec.acreditacion.infrastructure.persistence.postgres;

import com.undec.acreditacion.application.output.RoleRepository;
import com.undec.acreditacion.domain.entities.Role;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("!test")
public class PostgresRoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleRepository springDataRoleRepository;
    private final UserPersistenceMapper mapper;

    public PostgresRoleRepositoryAdapter(SpringDataRoleRepository springDataRoleRepository,
                                         UserPersistenceMapper mapper) {
        this.springDataRoleRepository = Objects.requireNonNull(springDataRoleRepository, "SpringDataRoleRepository is required");
        this.mapper = Objects.requireNonNull(mapper, "UserPersistenceMapper is required");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(UUID roleId) {
        return springDataRoleRepository.findById(roleId)
                .map(mapper::toDomainRole);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByCode(String code) {
        return springDataRoleRepository.findByCode(code)
                .map(mapper::toDomainRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return springDataRoleRepository.findAll().stream()
                .map(mapper::toDomainRole)
                .toList();
    }

    @Override
    @Transactional
    public Role save(Role role) {
        RoleJpaEntity entity = mapper.toEntityRole(role);
        RoleJpaEntity saved = springDataRoleRepository.save(entity);
        return mapper.toDomainRole(saved);
    }
}
