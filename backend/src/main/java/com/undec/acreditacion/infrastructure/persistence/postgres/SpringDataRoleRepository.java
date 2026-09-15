package com.undec.acreditacion.infrastructure.persistence.postgres;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataRoleRepository extends JpaRepository<RoleJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"permissions"})
    Optional<RoleJpaEntity> findByCode(String code);

    @Override
    @EntityGraph(attributePaths = {"permissions"})
    Optional<RoleJpaEntity> findById(UUID id);
}
