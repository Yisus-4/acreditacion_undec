package com.undec.acreditacion.infrastructure.persistence.postgres;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<UserJpaEntity> findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<UserJpaEntity> findById(UUID id);
}
