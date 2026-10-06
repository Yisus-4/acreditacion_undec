package com.undec.acreditacion.infrastructure.institucion.dto;

import com.undec.acreditacion.domain.entities.Provincia;

public record ProvinciaResponse(
        Long id,
        String nombre,
        String codigo
) {
    public static ProvinciaResponse fromDomain(Provincia domain) {
        if (domain == null) {
            return null;
        }
        return new ProvinciaResponse(domain.id(), domain.nombre(), domain.codigo());
    }
}
