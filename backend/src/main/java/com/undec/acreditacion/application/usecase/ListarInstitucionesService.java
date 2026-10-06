package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.ListarInstitucionesUseCase;
import com.undec.acreditacion.domain.entities.Institucion;
import com.undec.acreditacion.domain.ports.InstitucionRepositoryPort;

import java.util.List;
import java.util.Objects;

public class ListarInstitucionesService implements ListarInstitucionesUseCase {

    private final InstitucionRepositoryPort institucionRepository;

    public ListarInstitucionesService(InstitucionRepositoryPort institucionRepository) {
        this.institucionRepository = Objects.requireNonNull(institucionRepository, "institucionRepository must not be null");
    }

    @Override
    public List<Institucion> listar() {
        return institucionRepository.findAll();
    }
}
