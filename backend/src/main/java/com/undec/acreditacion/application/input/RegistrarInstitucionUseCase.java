package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.Institucion;

public interface RegistrarInstitucionUseCase {

    Institucion registrar(RegistrarInstitucionCommand command);
}
