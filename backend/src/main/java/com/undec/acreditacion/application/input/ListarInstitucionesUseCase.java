package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.Institucion;

import java.util.List;

public interface ListarInstitucionesUseCase {

    List<Institucion> listar();
}
