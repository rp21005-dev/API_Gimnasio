package gimnasio.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Prueba de integración: levanta toda la aplicación (controlador, servicio y base de datos)
// @Transactional deshace los cambios en la base de datos al terminar cada prueba
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EntrenadorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ENTRENADOR_JSON = """
            {
              "nombres": "Carlos",
              "apellidos": "Integracion",
              "correo": "carlos.integracion@ejemplo.com",
              "telefono": "7000-3333",
              "especialidad": "Crossfit",
              "contrasenaHash": "clave1234"
            }
            """;

    @Test
    @WithMockUser(username = "admin") // Simula un usuario autenticado
    @DisplayName("POST /gimnasioAPI/entrenador debe crear un entrenador y retornar 201 Created")
    void crearEntrenador_Exito() throws Exception {
        mockMvc.perform(post("/gimnasioAPI/entrenador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ENTRENADOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombreCompleto").value("Carlos Integracion"))
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("POST con datos inválidos debe retornar 400 Bad Request")
    void crearEntrenador_DatosInvalidos() throws Exception {
        mockMvc.perform(post("/gimnasioAPI/entrenador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\": \"no-es-correo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.correo").exists());
    }

    @Test
    @DisplayName("GET sin token debe retornar 401 Unauthorized")
    void listarEntrenadores_SinToken() throws Exception {
        mockMvc.perform(get("/gimnasioAPI/entrenador"))
                .andExpect(status().isUnauthorized());
    }
}
