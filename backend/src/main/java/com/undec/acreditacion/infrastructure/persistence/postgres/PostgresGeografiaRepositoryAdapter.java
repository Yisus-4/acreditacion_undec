package com.undec.acreditacion.infrastructure.persistence.postgres;

import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;
import com.undec.acreditacion.domain.ports.GeografiaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PostgresGeografiaRepositoryAdapter implements GeografiaRepositoryPort {

    private final SpringDataProvinciaRepository provinciaRepository;
    private final SpringDataDepartamentoRepository departamentoRepository;
    private final SpringDataLocalidadRepository localidadRepository;
    private final GeografiaPersistenceMapper mapper;

    public PostgresGeografiaRepositoryAdapter(SpringDataProvinciaRepository provinciaRepository,
                                            SpringDataDepartamentoRepository departamentoRepository,
                                            SpringDataLocalidadRepository localidadRepository,
                                            GeografiaPersistenceMapper mapper) {
        this.provinciaRepository = provinciaRepository;
        this.departamentoRepository = departamentoRepository;
        this.localidadRepository = localidadRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Provincia> findAllProvincias() {
        return provinciaRepository.findAllByOrderByNombreAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Provincia> findProvinciaById(Long id) {
        return provinciaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Departamento> findDepartamentosByProvinciaId(Long provinciaId) {
        return departamentoRepository.findByProvinciaIdOrderByNombreAsc(provinciaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Departamento> findDepartamentoById(Long id) {
        return departamentoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Localidad> findLocalidadesByDepartamentoId(Long departamentoId) {
        return localidadRepository.findByDepartamentoIdOrderByNombreAsc(departamentoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Localidad> findLocalidadById(Long id) {
        return localidadRepository.findById(id).map(mapper::toDomain);
    }
}
