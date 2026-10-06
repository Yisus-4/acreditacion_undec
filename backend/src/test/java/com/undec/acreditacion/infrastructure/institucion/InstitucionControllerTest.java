package com.undec.acreditacion.infrastructure.institucion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undec.acreditacion.application.input.ListarInstitucionesUseCase;
import com.undec.acreditacion.application.input.RegistrarInstitucionCommand;
import com.undec.acreditacion.application.input.RegistrarInstitucionUseCase;
import com.undec.acreditacion.domain.entities.AutoridadInstitucional;
import com.undec.acreditacion.domain.entities.Institucion;
import com.undec.acreditacion.domain.entities.SedeCentral;
import com.undec.acreditacion.domain.exceptions.ArchivoInvalidoException;
import com.undec.acreditacion.domain.exceptions.DomainValidationException;
import com.undec.acreditacion.domain.exceptions.InstitucionDuplicadaException;
import com.undec.acreditacion.domain.ports.DocumentStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InstitucionControllerTest {

    private MockMvc mockMvc;
    private RegistrarInstitucionUseCase registrarInstitucionUseCase;
    private ListarInstitucionesUseCase listarInstitucionesUseCase;
    private DocumentStoragePort documentStoragePort;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        registrarInstitucionUseCase = mock(RegistrarInstitucionUseCase.class);
        listarInstitucionesUseCase = mock(ListarInstitucionesUseCase.class);
        documentStoragePort = mock(DocumentStoragePort.class);
        objectMapper = new ObjectMapper();

        InstitucionController controller = new InstitucionController(
                registrarInstitucionUseCase,
                listarInstitucionesUseCase,
                documentStoragePort,
                objectMapper
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new InstitucionExceptionHandler())
                .build();
    }

    private Institucion buildSampleInstitucion() {
        AutoridadInstitucional maxAut = AutoridadInstitucional.maximaAutoridad(
                "Pérez", "Juan", null, "+543825111111", "rector@undec.edu.ar");
        AutoridadInstitucional adminAut = AutoridadInstitucional.administradorInstitucional(
                "Gómez", "María", null, "+543825222222", "admin@undec.edu.ar", "Secretario", "Rectorado");
        SedeCentral sede = SedeCentral.crear(
                "9 de Julio", "22", null, null, "5360", 1L, 1L, 1L);
        return Institucion.registrar(
                "Universidad Nacional de Chilecito",
                "UNdeC",
                "logos/undec.png",
                maxAut,
                adminAut,
                sede
        );
    }

    @Test
    @DisplayName("POST /api/instituciones multipart exitoso retorna 201 Created")
    void registrarInstitucionMultipartRetorna201() throws Exception {
        Institucion institucion = buildSampleInstitucion();
        when(registrarInstitucionUseCase.registrar(any(RegistrarInstitucionCommand.class)))
                .thenReturn(institucion);
        when(documentStoragePort.getFileUrl("logos/undec.png"))
                .thenReturn("http://localhost:9000/instituciones-logos/logos/undec.png");

        String datosJson = """
                {
                    "nombre": "Universidad Nacional de Chilecito",
                    "sigla": "UNdeC",
                    "maximaAutoridad": {
                        "apellido": "Pérez",
                        "primerNombre": "Juan",
                        "telefono": "+543825111111",
                        "email": "rector@undec.edu.ar"
                    },
                    "administradorInstitucional": {
                        "apellido": "Gómez",
                        "primerNombre": "María",
                        "telefono": "+543825222222",
                        "email": "admin@undec.edu.ar",
                        "cargo": "Secretario",
                        "ambito": "Rectorado"
                    },
                    "sedeCentral": {
                        "calle": "9 de Julio",
                        "numero": "22",
                        "codigoPostal": "5360",
                        "provinciaId": 1,
                        "departamentoId": 1,
                        "localidadId": 1
                    }
                }
                """;

        MockMultipartFile datosPart = new MockMultipartFile(
                "datos", "", "application/json", datosJson.getBytes());
        MockMultipartFile logoPart = new MockMultipartFile(
                "logotipo", "logo.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/instituciones")
                        .file(datosPart)
                        .file(logoPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Universidad Nacional de Chilecito"))
                .andExpect(jsonPath("$.sigla").value("UNdeC"))
                .andExpect(jsonPath("$.logoPath").value("logos/undec.png"))
                .andExpect(jsonPath("$.logoUrl").value("http://localhost:9000/instituciones-logos/logos/undec.png"))
                .andExpect(jsonPath("$.maximaAutoridad.apellido").value("Pérez"))
                .andExpect(jsonPath("$.administradorInstitucional.apellido").value("Gómez"))
                .andExpect(jsonPath("$.sedeCentral.calle").value("9 de Julio"));
    }

    @Test
    @DisplayName("POST /api/instituciones con institución duplicada retorna 409 Conflict")
    void registrarInstitucionDuplicadaRetorna409() throws Exception {
        when(registrarInstitucionUseCase.registrar(any(RegistrarInstitucionCommand.class)))
                .thenThrow(new InstitucionDuplicadaException("La institución ingresada ya se encuentra registrada en el sistema"));

        String datosJson = """
                {
                    "nombre": "Universidad Nacional de Chilecito",
                    "sigla": "UNdeC"
                }
                """;
        MockMultipartFile datosPart = new MockMultipartFile(
                "datos", "", "application/json", datosJson.getBytes());
        MockMultipartFile logoPart = new MockMultipartFile(
                "logotipo", "logo.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/instituciones")
                        .file(datosPart)
                        .file(logoPart))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La institución ingresada ya se encuentra registrada en el sistema"));
    }

    @Test
    @DisplayName("POST /api/instituciones con violación de integridad retorna 409 Conflict")
    void registrarInstitucionDataIntegrityViolationRetorna409() throws Exception {
        when(registrarInstitucionUseCase.registrar(any(RegistrarInstitucionCommand.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

        String datosJson = """
                {
                    "nombre": "Universidad Nacional de Chilecito",
                    "sigla": "UNdeC"
                }
                """;
        MockMultipartFile datosPart = new MockMultipartFile(
                "datos", "", "application/json", datosJson.getBytes());
        MockMultipartFile logoPart = new MockMultipartFile(
                "logotipo", "logo.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/instituciones")
                        .file(datosPart)
                        .file(logoPart))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflicto de integridad de datos: ya existe una institución con datos duplicados"));
    }

    @Test
    @DisplayName("POST /api/instituciones con formato de archivo inválido retorna 400 Bad Request")
    void registrarInstitucionArchivoInvalidoRetorna400() throws Exception {
        when(registrarInstitucionUseCase.registrar(any(RegistrarInstitucionCommand.class)))
                .thenThrow(new ArchivoInvalidoException("Debe adjuntar un formato de imagen válido (PNG o JPG)"));

        String datosJson = """
                {
                    "nombre": "Universidad Test",
                    "sigla": "UTEST"
                }
                """;
        MockMultipartFile datosPart = new MockMultipartFile(
                "datos", "", "application/json", datosJson.getBytes());
        MockMultipartFile logoPart = new MockMultipartFile(
                "logotipo", "documento.pdf", "application/pdf", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/instituciones")
                        .file(datosPart)
                        .file(logoPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Debe adjuntar un formato de imagen válido (PNG o JPG)"));
    }

    @Test
    @DisplayName("POST /api/instituciones con datos faltantes retorna 400 Bad Request")
    void registrarInstitucionDatosFaltantesRetorna400() throws Exception {
        when(registrarInstitucionUseCase.registrar(any(RegistrarInstitucionCommand.class)))
                .thenThrow(new DomainValidationException("El nombre de la institución es obligatorio"));

        String datosJson = "{}";
        MockMultipartFile datosPart = new MockMultipartFile(
                "datos", "", "application/json", datosJson.getBytes());

        mockMvc.perform(multipart("/api/instituciones")
                        .file(datosPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre de la institución es obligatorio"));
    }

    @Test
    @DisplayName("GET /api/instituciones retorna lista con 200 OK")
    void listarInstitucionesRetorna200() throws Exception {
        Institucion institucion = buildSampleInstitucion();
        when(listarInstitucionesUseCase.listar()).thenReturn(List.of(institucion));
        when(documentStoragePort.getFileUrl("logos/undec.png"))
                .thenReturn("http://localhost:9000/instituciones-logos/logos/undec.png");

        mockMvc.perform(get("/api/instituciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Universidad Nacional de Chilecito"))
                .andExpect(jsonPath("$[0].sigla").value("UNdeC"))
                .andExpect(jsonPath("$[0].logoUrl").value("http://localhost:9000/instituciones-logos/logos/undec.png"));
    }
}
