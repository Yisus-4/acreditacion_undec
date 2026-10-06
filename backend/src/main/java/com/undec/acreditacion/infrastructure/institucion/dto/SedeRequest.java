package com.undec.acreditacion.infrastructure.institucion.dto;

public record SedeRequest(
        String calle,
        String numero,
        String piso,
        String departamento,
        String codigoPostal,
        Long provinciaId,
        Long departamentoId,
        Long localidadId
) {}
