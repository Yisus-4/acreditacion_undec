package com.undec.acreditacion.infrastructure.institucion.dto;

import com.undec.acreditacion.domain.entities.SedeCentral;

import java.time.Instant;
import java.util.UUID;

public record SedeResponse(
        UUID id,
        boolean esSedeCentral,
        String calle,
        String numero,
        String piso,
        String departamento,
        String codigoPostal,
        Long provinciaId,
        Long departamentoId,
        Long localidadId,
        String direccionCompleta,
        Instant creadoEn
) {
    public static SedeResponse fromDomain(SedeCentral domain) {
        if (domain == null) {
            return null;
        }
        return new SedeResponse(
                domain.getId(),
                domain.isEsSedeCentral(),
                domain.getCalle(),
                domain.getNumero(),
                domain.getPiso(),
                domain.getDepartamento(),
                domain.getCodigoPostal(),
                domain.getProvinciaId(),
                domain.getDepartamentoId(),
                domain.getLocalidadId(),
                domain.getDireccionCompleta(),
                domain.getCreadoEn()
        );
    }
}
