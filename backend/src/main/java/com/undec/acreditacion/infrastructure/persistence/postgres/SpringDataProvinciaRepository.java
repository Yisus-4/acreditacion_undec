package com.undec.acreditacion.infrastructure.persistence.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataProvinciaRepository extends JpaRepository<ProvinciaJpaEntity, Long> {

    List<ProvinciaJpaEntity> findAllByOrderByNombreAsc();

    Optional<ProvinciaJpaEntity> findByCodigo(String codigo);
}
