package com.undec.acreditacion.infrastructure.institucion.dto;

public record AutoridadRequest(
        String apellido,
        String primerNombre,
        String segundoNombre,
        String telefono,
        String email,
        String cargo,
        String ambito
) {}
