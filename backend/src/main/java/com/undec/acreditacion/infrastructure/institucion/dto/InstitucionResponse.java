package com.undec.acreditacion.infrastructure.institucion.dto;

import com.undec.acreditacion.domain.entities.Institucion;
import com.undec.acreditacion.domain.ports.DocumentStoragePort;

import java.time.Instant;
import java.util.UUID;

public record InstitucionResponse(
        UUID id,
        String nombre,
        String sigla,
        String logoPath,
        String logoUrl,
        boolean activo,
        Instant creadoEn,
        Instant actualizadoEn,
        AutoridadResponse maximaAutoridad,
        AutoridadResponse administradorInstitucional,
        SedeResponse sedeCentral
) {
    public static InstitucionResponse fromDomain(Institucion domain, DocumentStoragePort storagePort) {
        if (domain == null) {
            return null;
        }

        String logoUrl = null;
        if (storagePort != null && domain.getLogoPath() != null) {
            logoUrl = storagePort.getFileUrl(domain.getLogoPath());
        }

        AutoridadResponse maxAut = domain.getMaximaAutoridad()
                .map(AutoridadResponse::fromDomain)
                .orElse(null);

        AutoridadResponse adminAut = domain.getAdministradorInstitucional()
                .map(AutoridadResponse::fromDomain)
                .orElse(null);

        SedeResponse sede = SedeResponse.fromDomain(domain.getSedeCentral());

        return new InstitucionResponse(
                domain.getId(),
                domain.getNombre(),
                domain.getSigla(),
                domain.getLogoPath(),
                logoUrl,
                domain.isActivo(),
                domain.getCreadoEn(),
                domain.getActualizadoEn(),
                maxAut,
                adminAut,
                sede
        );
    }
}
