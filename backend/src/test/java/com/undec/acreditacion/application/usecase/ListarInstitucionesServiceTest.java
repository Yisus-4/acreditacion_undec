package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.domain.entities.AutoridadInstitucional;
import com.undec.acreditacion.domain.entities.Institucion;
import com.undec.acreditacion.domain.entities.SedeCentral;
import com.undec.acreditacion.domain.ports.InstitucionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListarInstitucionesServiceTest {

    private InstitucionRepositoryPort institucionRepository;
    private ListarInstitucionesService service;

    @BeforeEach
    void setUp() {
        institucionRepository = mock(InstitucionRepositoryPort.class);
        service = new ListarInstitucionesService(institucionRepository);
    }

    @Test
    @DisplayName("listar retorna instituciones del repositorio")
    void listarRetornaInstituciones() {
        AutoridadInstitucional maxAut = AutoridadInstitucional.maximaAutoridad(
                "Pérez", "Juan", null, "123", "rector@undec.edu.ar");
        AutoridadInstitucional adminAut = AutoridadInstitucional.administradorInstitucional(
                "Gómez", "María", null, "456", "admin@undec.edu.ar", "Sec", "Rec");
        SedeCentral sede = SedeCentral.crear(
                "9 de Julio", "22", null, null, "5360", 1L, 1L, 1L);
        Institucion inst = Institucion.registrar(
                "UNdeC", "UNdeC", "logo.png", maxAut, adminAut, sede);

        when(institucionRepository.findAll()).thenReturn(List.of(inst));

        List<Institucion> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("UNdeC", resultado.get(0).getNombre());
        verify(institucionRepository).findAll();
    }
}
