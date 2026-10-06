package com.undec.acreditacion.infrastructure.persistence.postgres;

import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;
import org.springframework.stereotype.Component;

@Component
public class GeografiaPersistenceMapper {

    public Provincia toDomain(ProvinciaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Provincia(entity.getId(), entity.getNombre(), entity.getCodigo());
    }

    public Departamento toDomain(DepartamentoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Departamento(
                entity.getId(),
                entity.getProvincia() != null ? entity.getProvincia().getId() : null,
                entity.getNombre()
        );
    }

    public Localidad toDomain(LocalidadJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Localidad(
                entity.getId(),
                entity.getDepartamento() != null ? entity.getDepartamento().getId() : null,
                entity.getNombre(),
                entity.getCodigoPostal()
        );
    }
}
