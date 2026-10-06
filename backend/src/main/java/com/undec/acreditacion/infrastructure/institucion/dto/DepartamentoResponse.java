package com.undec.acreditacion.infrastructure.institucion.dto;

import com.undec.acreditacion.domain.entities.Departamento;

public record DepartamentoResponse(
        Long id,
        Long provinciaId,
        String nombre
) {
    public static DepartamentoResponse fromDomain(Departamento domain) {
        if (domain == null) {
            return null;
        }
        return new DepartamentoResponse(domain.id(), domain.provinciaId(), domain.nombre());
    }
}
