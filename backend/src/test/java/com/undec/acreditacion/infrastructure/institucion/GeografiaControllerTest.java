package com.undec.acreditacion.infrastructure.institucion;

import com.undec.acreditacion.application.input.ConsultarGeografiaUseCase;
import com.undec.acreditacion.domain.entities.Departamento;
import com.undec.acreditacion.domain.entities.Localidad;
import com.undec.acreditacion.domain.entities.Provincia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GeografiaControllerTest {

    private MockMvc mockMvc;
    private ConsultarGeografiaUseCase consultarGeografiaUseCase;

    @BeforeEach
    void setUp() {
        consultarGeografiaUseCase = mock(ConsultarGeografiaUseCase.class);
        GeografiaController controller = new GeografiaController(consultarGeografiaUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new InstitucionExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/geografia/provincias retorna lista con 200 OK")
    void listarProvinciasRetorna200() throws Exception {
        Provincia laRioja = new Provincia(1L, "La Rioja", "LR");
        when(consultarGeografiaUseCase.listarProvincias()).thenReturn(List.of(laRioja));

        mockMvc.perform(get("/api/geografia/provincias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("La Rioja"))
                .andExpect(jsonPath("$[0].codigo").value("LR"));
    }

    @Test
    @DisplayName("GET /api/geografia/provincias/{id}/departamentos retorna lista con 200 OK")
    void listarDepartamentosRetorna200() throws Exception {
        Departamento chilecito = new Departamento(10L, 1L, "Chilecito");
        when(consultarGeografiaUseCase.listarDepartamentosPorProvincia(1L)).thenReturn(List.of(chilecito));

        mockMvc.perform(get("/api/geografia/provincias/1/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nombre").value("Chilecito"))
                .andExpect(jsonPath("$[0].provinciaId").value(1));
    }

    @Test
    @DisplayName("GET /api/geografia/departamentos/{id}/localidades retorna lista con 200 OK")
    void listarLocalidadesRetorna200() throws Exception {
        Localidad chilecitoLoc = new Localidad(100L, 10L, "Chilecito", "5360");
        when(consultarGeografiaUseCase.listarLocalidadesPorDepartamento(10L)).thenReturn(List.of(chilecitoLoc));

        mockMvc.perform(get("/api/geografia/departamentos/10/localidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].nombre").value("Chilecito"))
                .andExpect(jsonPath("$[0].codigoPostal").value("5360"));
    }
}
