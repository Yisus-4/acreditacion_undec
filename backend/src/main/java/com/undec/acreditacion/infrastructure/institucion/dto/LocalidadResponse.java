package com.undec.acreditacion.infrastructure.institucion.dto;

import com.undec.acreditacion.domain.entities.Localidad;

public record LocalidadResponse(
        Long id,
        Long departamentoId,
        String nombre,
        String codigoPostal
) {
    public static LocalidadResponse fromDomain(Localidad domain) {
        if (domain == null) {
            return null;
        }
        return new LocalidadResponse(domain.id(), domain.departamentoId(), domain.nombre(), domain.codigoPostal());
    }
}
