package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;
import com.undec.acreditacion.domain.ports.GeografiaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultarGeografiaServiceTest {

    private GeografiaRepositoryPort repository;
    private ConsultarGeografiaService service;

    @BeforeEach
    void setUp() {
        repository = mock(GeografiaRepositoryPort.class);
        service = new ConsultarGeografiaService(repository);
    }

    @Test
    @DisplayName("listarProvincias retorna todas las provincias")
    void listarProvinciasRetornaTodas() {
        Provincia lr = new Provincia(1L, "La Rioja", "LR");
        when(repository.findAllProvincias()).thenReturn(List.of(lr));

        List<Provincia> result = service.listarProvincias();

        assertEquals(1, result.size());
        assertEquals("La Rioja", result.get(0).nombre());
        verify(repository).findAllProvincias();
    }

    @Test
    @DisplayName("listarDepartamentosPorProvincia con id nulo retorna lista vacía")
    void listarDepartamentosNullRetornaVacia() {
        List<Departamento> result = service.listarDepartamentosPorProvincia(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("listarDepartamentosPorProvincia retorna departamentos")
    void listarDepartamentosRetornaLista() {
        Departamento chilecito = new Departamento(10L, 1L, "Chilecito");
        when(repository.findDepartamentosByProvinciaId(1L)).thenReturn(List.of(chilecito));

        List<Departamento> result = service.listarDepartamentosPorProvincia(1L);

        assertEquals(1, result.size());
        assertEquals("Chilecito", result.get(0).nombre());
        verify(repository).findDepartamentosByProvinciaId(1L);
    }

    @Test
    @DisplayName("listarLocalidadesPorDepartamento con id nulo retorna lista vacía")
    void listarLocalidadesNullRetornaVacia() {
        List<Localidad> result = service.listarLocalidadesPorDepartamento(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("listarLocalidadesPorDepartamento retorna localidades")
    void listarLocalidadesRetornaLista() {
        Localidad loc = new Localidad(100L, 10L, "Chilecito", "5360");
        when(repository.findLocalidadesByDepartamentoId(10L)).thenReturn(List.of(loc));

        List<Localidad> result = service.listarLocalidadesPorDepartamento(10L);

        assertEquals(1, result.size());
        assertEquals("Chilecito", result.get(0).nombre());
        verify(repository).findLocalidadesByDepartamentoId(10L);
    }
}
