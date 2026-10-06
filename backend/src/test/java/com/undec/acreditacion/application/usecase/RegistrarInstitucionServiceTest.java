package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.RegistrarInstitucionCommand;
import com.undec.acreditacion.domain.entities.Institucion;
import com.undec.acreditacion.domain.entities.TipoAutoridad;
import com.undec.acreditacion.domain.exceptions.ArchivoInvalidoException;
import com.undec.acreditacion.domain.exceptions.DomainValidationException;
import com.undec.acreditacion.domain.exceptions.InstitucionDuplicadaException;
import com.undec.acreditacion.domain.ports.DocumentStoragePort;
import com.undec.acreditacion.domain.ports.InstitucionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrarInstitucionServiceTest {

    private InstitucionRepositoryPort institucionRepository;
    private DocumentStoragePort documentStoragePort;
    private RegistrarInstitucionService service;

    @BeforeEach
    void setUp() {
        institucionRepository = mock(InstitucionRepositoryPort.class);
        documentStoragePort = mock(DocumentStoragePort.class);
        service = new RegistrarInstitucionService(institucionRepository, documentStoragePort);
    }

    private RegistrarInstitucionCommand createValidCommand() {
        return new RegistrarInstitucionCommand(
                "Universidad Nacional de Chilecito",
                "UNdeC",
                new byte[]{1, 2, 3, 4},
                "logo.png",
                "image/png",
                "Pérez",
                "Juan",
                "Carlos",
                "+543825123456",
                "rector@undec.edu.ar",
                "Gómez",
                "María",
                null,
                "+543825654321",
                "admin@undec.edu.ar",
                "Secretario General",
                "Rectorado",
                "9 de Julio",
                "22",
                "1",
                "A",
                "5360",
                1L,
                1L,
                1L
        );
    }

    @Test
    @DisplayName("Registrar institución exitosamente")
    void registrarInstitucionExitoso() {
        RegistrarInstitucionCommand command = createValidCommand();

        when(institucionRepository.existsByNombre(command.nombre().trim())).thenReturn(false);
        when(institucionRepository.existsBySigla(command.sigla().trim())).thenReturn(false);
        when(documentStoragePort.uploadFile(eq(command.logoBytes()), anyString(), anyString()))
                .thenReturn("logos/unique-logo.png");
        when(institucionRepository.save(any(Institucion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Institucion resultado = service.registrar(command);

        assertNotNull(resultado);
        assertEquals("Universidad Nacional de Chilecito", resultado.getNombre());
        assertEquals("UNdeC", resultado.getSigla());
        assertEquals("logos/unique-logo.png", resultado.getLogoPath());
        assertTrue(resultado.isActivo());
        assertEquals(2, resultado.getAutoridades().size());
        assertTrue(resultado.getMaximaAutoridad().isPresent());
        assertEquals(TipoAutoridad.MAXIMA_AUTORIDAD, resultado.getMaximaAutoridad().get().getTipoAutoridad());
        assertEquals("Pérez", resultado.getMaximaAutoridad().get().getApellido());
        assertTrue(resultado.getAdministradorInstitucional().isPresent());
        assertEquals("Gómez", resultado.getAdministradorInstitucional().get().getApellido());
        assertNotNull(resultado.getSedeCentral());
        assertEquals("9 de Julio", resultado.getSedeCentral().getCalle());
        assertEquals("22", resultado.getSedeCentral().getNumero());

        verify(institucionRepository).existsByNombre("Universidad Nacional de Chilecito");
        verify(institucionRepository).existsBySigla("UNdeC");
        verify(documentStoragePort).uploadFile(command.logoBytes(), "logo.png", "image/png");
        verify(institucionRepository).save(any(Institucion.class));
    }

    @Test
    @DisplayName("Registrar institución duplicada lanza InstitucionDuplicadaException")
    void registrarInstitucionDuplicadaLanzaExcepcion() {
        RegistrarInstitucionCommand command = createValidCommand();

        when(institucionRepository.existsByNombre(command.nombre().trim())).thenReturn(true);

        InstitucionDuplicadaException ex = assertThrows(InstitucionDuplicadaException.class, () ->
                service.registrar(command));

        assertEquals("La institución ingresada ya se encuentra registrada en el sistema", ex.getMessage());
        verify(documentStoragePort, never()).uploadFile(any(), any(), any());
        verify(institucionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registrar institución con sigla duplicada lanza InstitucionDuplicadaException")
    void registrarInstitucionSiglaDuplicadaLanzaExcepcion() {
        RegistrarInstitucionCommand command = createValidCommand();

        when(institucionRepository.existsByNombre(command.nombre().trim())).thenReturn(false);
        when(institucionRepository.existsBySigla(command.sigla().trim())).thenReturn(true);

        InstitucionDuplicadaException ex = assertThrows(InstitucionDuplicadaException.class, () ->
                service.registrar(command));

        assertEquals("La institución ingresada ya se encuentra registrada en el sistema", ex.getMessage());
        verify(documentStoragePort, never()).uploadFile(any(), any(), any());
        verify(institucionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fallo al guardar en repositorio compensa eliminando archivo en storage")
    void registrarInstitucionFallaGuardadoCompensaStorage() {
        RegistrarInstitucionCommand command = createValidCommand();

        when(institucionRepository.existsByNombre(command.nombre().trim())).thenReturn(false);
        when(institucionRepository.existsBySigla(command.sigla().trim())).thenReturn(false);
        when(documentStoragePort.uploadFile(eq(command.logoBytes()), anyString(), anyString()))
                .thenReturn("logos/uploaded-logo.png");
        when(institucionRepository.save(any(Institucion.class)))
                .thenThrow(new RuntimeException("Database error al persistir institución"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.registrar(command));

        assertEquals("Database error al persistir institución", ex.getMessage());
        verify(documentStoragePort).uploadFile(command.logoBytes(), "logo.png", "image/png");
        verify(institucionRepository).save(any(Institucion.class));
        verify(documentStoragePort).deleteFile("logos/uploaded-logo.png");
    }

    @Test
    @DisplayName("Registrar institución con formato de archivo no imagen lanza ArchivoInvalidoException")
    void registrarInstitucionImagenInvalidaFormato() {
        RegistrarInstitucionCommand command = new RegistrarInstitucionCommand(
                "Universidad Test",
                "UTEST",
                new byte[]{1, 2, 3},
                "documento.pdf",
                "application/pdf",
                "Pérez", "Juan", null, "123456", "rector@test.edu.ar",
                "Gómez", "María", null, "654321", "admin@test.edu.ar", "Cargo", "Ambito",
                "Calle", "100", null, null, "5000",
                1L, 1L, 1L
        );

        when(institucionRepository.existsByNombre(anyString())).thenReturn(false);

        ArchivoInvalidoException ex = assertThrows(ArchivoInvalidoException.class, () ->
                service.registrar(command));

        assertTrue(ex.getMessage().contains("Debe adjuntar un formato de imagen válido (PNG o JPG)"));
        verify(documentStoragePort, never()).uploadFile(any(), any(), any());
    }

    @Test
    @DisplayName("Registrar institución sin archivo de logotipo lanza ArchivoInvalidoException")
    void registrarInstitucionSinLogoLanzaExcepcion() {
        RegistrarInstitucionCommand command = new RegistrarInstitucionCommand(
                "Universidad Test",
                "UTEST",
                null,
                null,
                null,
                "Pérez", "Juan", null, "123456", "rector@test.edu.ar",
                "Gómez", "María", null, "654321", "admin@test.edu.ar", "Cargo", "Ambito",
                "Calle", "100", null, null, "5000",
                1L, 1L, 1L
        );

        when(institucionRepository.existsByNombre(anyString())).thenReturn(false);

        assertThrows(ArchivoInvalidoException.class, () -> service.registrar(command));
        verify(documentStoragePort, never()).uploadFile(any(), any(), any());
    }

    @Test
    @DisplayName("Registrar institución con campos obligatorios faltantes lanza DomainValidationException")
    void registrarInstitucionCamposFaltantesLanzaExcepcion() {
        RegistrarInstitucionCommand commandSinNombre = new RegistrarInstitucionCommand(
                "",
                "UNdeC",
                new byte[]{1, 2},
                "logo.png",
                "image/png",
                "Pérez", "Juan", null, "123", "rector@undec.edu.ar",
                "Gómez", "María", null, "456", "admin@undec.edu.ar", "Cargo", "Ambito",
                "Calle", "1", null, null, "5360",
                1L, 1L, 1L
        );

        assertThrows(DomainValidationException.class, () -> service.registrar(commandSinNombre));

        RegistrarInstitucionCommand commandSinSede = new RegistrarInstitucionCommand(
                "UNdeC",
                "UNdeC",
                new byte[]{1, 2},
                "logo.png",
                "image/png",
                "Pérez", "Juan", null, "123", "rector@undec.edu.ar",
                "Gómez", "María", null, "456", "admin@undec.edu.ar", "Cargo", "Ambito",
                null, "1", null, null, "5360",
                1L, 1L, 1L
        );

        assertThrows(DomainValidationException.class, () -> service.registrar(commandSinSede));
    }

    @Test
    @DisplayName("Registrar institución con formato de email inválido lanza DomainValidationException")
    void registrarInstitucionCorreoInvalidoLanzaExcepcion() {
        RegistrarInstitucionCommand command = new RegistrarInstitucionCommand(
                "UNdeC",
                "UNdeC",
                new byte[]{1, 2},
                "logo.png",
                "image/png",
                "Pérez", "Juan", null, "123", "email-invalido",
                "Gómez", "María", null, "456", "admin@undec.edu.ar", "Cargo", "Ambito",
                "Calle", "1", null, null, "5360",
                1L, 1L, 1L
        );

        assertThrows(DomainValidationException.class, () -> service.registrar(command));
    }
}
