package com.undec.acreditacion.infrastructure.persistence.postgres;

import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresGeografiaRepositoryAdapterTest {

    private SpringDataProvinciaRepository provinciaRepository;
    private SpringDataDepartamentoRepository departamentoRepository;
    private SpringDataLocalidadRepository localidadRepository;
    private GeografiaPersistenceMapper mapper;
    private PostgresGeografiaRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        provinciaRepository = mock(SpringDataProvinciaRepository.class);
        departamentoRepository = mock(SpringDataDepartamentoRepository.class);
        localidadRepository = mock(SpringDataLocalidadRepository.class);
        mapper = mock(GeografiaPersistenceMapper.class);
        adapter = new PostgresGeografiaRepositoryAdapter(
                provinciaRepository,
                departamentoRepository,
                localidadRepository,
                mapper
        );
    }

    @Test
    @DisplayName("findAllProvincias delega y mapea")
    void findAllProvinciasDelega() {
        ProvinciaJpaEntity entity = new ProvinciaJpaEntity();
        Provincia domain = new Provincia(1L, "La Rioja", "LR");

        when(provinciaRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Provincia> result = adapter.findAllProvincias();

        assertEquals(1, result.size());
        assertEquals("La Rioja", result.get(0).nombre());
        verify(provinciaRepository).findAllByOrderByNombreAsc();
    }

    @Test
    @DisplayName("findDepartamentosByProvinciaId delega y mapea")
    void findDepartamentosDelega() {
        DepartamentoJpaEntity entity = new DepartamentoJpaEntity();
        Departamento domain = new Departamento(10L, 1L, "Chilecito");

        when(departamentoRepository.findByProvinciaIdOrderByNombreAsc(1L)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Departamento> result = adapter.findDepartamentosByProvinciaId(1L);

        assertEquals(1, result.size());
        assertEquals("Chilecito", result.get(0).nombre());
        verify(departamentoRepository).findByProvinciaIdOrderByNombreAsc(1L);
    }

    @Test
    @DisplayName("findLocalidadesByDepartamentoId delega y mapea")
    void findLocalidadesDelega() {
        LocalidadJpaEntity entity = new LocalidadJpaEntity();
        Localidad domain = new Localidad(100L, 10L, "Chilecito", "5360");

        when(localidadRepository.findByDepartamentoIdOrderByNombreAsc(10L)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Localidad> result = adapter.findLocalidadesByDepartamentoId(10L);

        assertEquals(1, result.size());
        assertEquals("Chilecito", result.get(0).nombre());
        verify(localidadRepository).findByDepartamentoIdOrderByNombreAsc(10L);
    }
}
