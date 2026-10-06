package com.undec.acreditacion.domain.ports;

import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;

import java.util.List;
import java.util.Optional;

public interface GeografiaRepositoryPort {

    List<Provincia> findAllProvincias();

    Optional<Provincia> findProvinciaById(Long id);

    List<Departamento> findDepartamentosByProvinciaId(Long provinciaId);

    Optional<Departamento> findDepartamentoById(Long id);

    List<Localidad> findLocalidadesByDepartamentoId(Long departamentoId);

    Optional<Localidad> findLocalidadById(Long id);
}
