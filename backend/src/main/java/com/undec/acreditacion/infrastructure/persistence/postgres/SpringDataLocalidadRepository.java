package com.undec.acreditacion.infrastructure.persistence.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataLocalidadRepository extends JpaRepository<LocalidadJpaEntity, Long> {

    List<LocalidadJpaEntity> findByDepartamentoIdOrderByNombreAsc(Long departamentoId);
}
