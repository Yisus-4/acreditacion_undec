package com.undec.acreditacion.domain.entities;

public record Localidad(
        Long id,
        Long departamentoId,
        String nombre,
        String codigoPostal
) {}
