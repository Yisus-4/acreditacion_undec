package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.ConsultarGeografiaUseCase;
import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;
import com.undec.acreditacion.domain.ports.GeografiaRepositoryPort;

import java.util.List;
import java.util.Objects;

public class ConsultarGeografiaService implements ConsultarGeografiaUseCase {

    private final GeografiaRepositoryPort geografiaRepository;

    public ConsultarGeografiaService(GeografiaRepositoryPort geografiaRepository) {
        this.geografiaRepository = Objects.requireNonNull(geografiaRepository, "geografiaRepository must not be null");
    }

    @Override
    public List<Provincia> listarProvincias() {
        return geografiaRepository.findAllProvincias();
    }

    @Override
    public List<Departamento> listarDepartamentosPorProvincia(Long provinciaId) {
        if (provinciaId == null) {
            return List.of();
        }
        return geografiaRepository.findDepartamentosByProvinciaId(provinciaId);
    }

    @Override
    public List<Localidad> listarLocalidadesPorDepartamento(Long departamentoId) {
        if (departamentoId == null) {
            return List.of();
        }
        return geografiaRepository.findLocalidadesByDepartamentoId(departamentoId);
    }
}
