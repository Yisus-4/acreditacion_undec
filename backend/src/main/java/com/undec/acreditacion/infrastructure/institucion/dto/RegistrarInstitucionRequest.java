package com.undec.acreditacion.infrastructure.institucion.dto;

public record RegistrarInstitucionRequest(
        String nombre,
        String sigla,
        AutoridadRequest maximaAutoridad,
        AutoridadRequest administradorInstitucional,
        SedeRequest sedeCentral
) {}
