package com.undec.acreditacion.infrastructure.institucion.dto;

import com.undec.acreditacion.domain.entities.AutoridadInstitucional;

import java.time.Instant;
import java.util.UUID;

public record AutoridadResponse(
        UUID id,
        String tipoAutoridad,
        String apellido,
        String primerNombre,
        String segundoNombre,
        String nombreCompleto,
        String telefono,
        String email,
        String cargo,
        String ambito,
        Instant creadoEn
) {
    public static AutoridadResponse fromDomain(AutoridadInstitucional domain) {
        if (domain == null) {
            return null;
        }
        return new AutoridadResponse(
                domain.getId(),
                domain.getTipoAutoridad().name(),
                domain.getApellido(),
                domain.getPrimerNombre(),
                domain.getSegundoNombre(),
                domain.getNombreCompleto(),
                domain.getTelefono(),
                domain.getEmail(),
                domain.getCargo(),
                domain.getAmbito(),
                domain.getCreadoEn()
        );
    }
}
