package com.undec.acreditacion.domain.entities;

public record Departamento(
        Long id,
        Long provinciaId,
        String nombre
) {}
