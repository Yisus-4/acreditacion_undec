package com.undec.acreditacion.application.input;

public record RegistrarInstitucionCommand(
        // Bloque 1: Informacion de la Institucion
        String nombre,
        String sigla,
        byte[] logoBytes,
        String logoFilename,
        String logoContentType,

        // Bloque 2: Maxima Autoridad
        String maximaAutoridadApellido,
        String maximaAutoridadPrimerNombre,
        String maximaAutoridadSegundoNombre,
        String maximaAutoridadTelefono,
        String maximaAutoridadEmail,

        // Bloque 3: Administrador Institucional
        String adminInstitucionalApellido,
        String adminInstitucionalPrimerNombre,
        String adminInstitucionalSegundoNombre,
        String adminInstitucionalTelefono,
        String adminInstitucionalEmail,
        String adminInstitucionalCargo,
        String adminInstitucionalAmbito,

        // Bloque 4: Direccion de la Sede Central
        String calle,
        String numero,
        String piso,
        String departamento,
        String codigoPostal,
        Long provinciaId,
        Long departamentoId,
        Long localidadId
) {}
