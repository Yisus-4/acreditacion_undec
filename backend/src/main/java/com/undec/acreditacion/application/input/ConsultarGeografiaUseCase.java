package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;

import java.util.List;

public interface ConsultarGeografiaUseCase {

    List<Provincia> listarProvincias();

    List<Departamento> listarDepartamentosPorProvincia(Long provinciaId);

    List<Localidad> listarLocalidadesPorDepartamento(Long departamentoId);
}
