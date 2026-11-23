package gimnasio.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Prueba de integración de los permisos por rol con tokens reales de los usuarios de demostración
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Cada prueba deshace lo que crea
class RolesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Inicia sesión y devuelve la respuesta completa (token, rol e id)
    private String login(String correo) throws Exception {
        return mockMvc.perform(post("/gimnasioAPI/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\": \"%s\", \"contrasena\": \"clave1234\"}".formatted(correo)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String token(String correo) throws Exception {
        return "Bearer " + JsonPath.read(login(correo), "$.token");
    }

    private ResultActions postJson(String url, String token, String json) throws Exception {
        return mockMvc.perform(post(url).header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private Long primeraClase(String token) throws Exception {
        String clases = mockMvc.perform(get("/gimnasioAPI/clases").header("Authorization", token))
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(clases, "$[0].id")).longValue();
    }

    @Test
    @DisplayName("El login devuelve el ID del usuario")
    void loginDevuelveId() throws Exception {
        String miembro = login("miembro@gimnasio.com");
        org.junit.jupiter.api.Assertions.assertNotNull(JsonPath.read(miembro, "$.id"));
    }

    @Test
    @DisplayName("El miembro consulta el catálogo, pero no gestiona usuarios ni catálogos (403)")
    void permisosDelMiembro() throws Exception {
        String miembro = token("miembro@gimnasio.com");

        mockMvc.perform(get("/gimnasioAPI/clases").header("Authorization", miembro)).andExpect(status().isOk());
        mockMvc.perform(get("/gimnasioAPI/salas").header("Authorization", miembro)).andExpect(status().isOk());

        mockMvc.perform(get("/gimnasioAPI/administrador").header("Authorization", miembro))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
        mockMvc.perform(get("/gimnasioAPI/miembros").header("Authorization", miembro)).andExpect(status().isForbidden());
        postJson("/gimnasioAPI/salas", miembro, "{}").andExpect(status().isForbidden());
        postJson("/gimnasioAPI/clases", miembro, "{}").andExpect(status().isForbidden());
        postJson("/gimnasioAPI/asistencias", miembro, "{}").andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("El entrenador consulta miembros y gestiona clases, pero no salas ni administradores")
    void permisosDelEntrenador() throws Exception {
        String entrenador = token("entrenador@gimnasio.com");

        mockMvc.perform(get("/gimnasioAPI/miembros").header("Authorization", entrenador)).andExpect(status().isOk());
        // Pasa el filtro de roles y llega a la validación de datos (400), no 403
        postJson("/gimnasioAPI/clases", entrenador, "{\"cupoMaximo\": 0, \"diasSemana\": []}").andExpect(status().isBadRequest());

        postJson("/gimnasioAPI/salas", entrenador, "{}").andExpect(status().isForbidden());
        mockMvc.perform(get("/gimnasioAPI/administrador").header("Authorization", entrenador)).andExpect(status().isForbidden());
        postJson("/gimnasioAPI/inscripciones", entrenador, "{}").andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("El miembro se inscribe a sí mismo, aunque envíe el ID de otro miembro")
    void miembroSeInscribeASiMismo() throws Exception {
        String sesion = login("miembro@gimnasio.com");
        String miembro = "Bearer " + JsonPath.read(sesion, "$.token");
        Long miId = Long.valueOf(JsonPath.read(sesion, "$.id"));
        Long claseId = primeraClase(miembro);

        postJson("/gimnasioAPI/inscripciones", miembro, "{\"claseId\": %d, \"miembroId\": 999}".formatted(claseId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.miembroId").value(miId));

        // Al listar, el miembro solo recibe sus propias inscripciones
        mockMvc.perform(get("/gimnasioAPI/inscripciones").header("Authorization", miembro))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].miembroId", everyItem(is(miId.intValue()))));
    }

    @Test
    @DisplayName("El miembro no puede ver ni cancelar la inscripción de otro miembro (403)")
    void miembroNoTocaLoAjeno() throws Exception {
        String admin = token("admin@gimnasio.com");
        String miembro = token("miembro@gimnasio.com");
        Long claseId = primeraClase(admin);

        // Otro miembro, inscrito por el administrador
        String otro = mockMvc.perform(post("/gimnasioAPI/miembros").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nombres": "Otro", "apellidos": "Miembro", "documentoIdentidad": "DUI-ROLES-1",
                         "correo": "otro.roles@ejemplo.com", "telefono": "7000-0000",
                         "fechaNacimiento": "2000-01-01", "contrasenaHash": "clave1234"}
                        """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long otroId = ((Number) JsonPath.read(otro, "$.id")).longValue();

        String inscripcion = postJson("/gimnasioAPI/inscripciones", admin,
                "{\"claseId\": %d, \"miembroId\": %d}".formatted(claseId, otroId))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long inscripcionId = ((Number) JsonPath.read(inscripcion, "$.id")).longValue();

        mockMvc.perform(get("/gimnasioAPI/inscripciones/{id}", inscripcionId).header("Authorization", miembro))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/gimnasioAPI/inscripciones/{id}", inscripcionId).header("Authorization", miembro))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/gimnasioAPI/inscripciones/{id}", inscripcionId).header("Authorization", admin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un JSON mal formado devuelve 400 con un mensaje claro")
    void jsonMalFormado() throws Exception {
        postJson("/gimnasioAPI/salas", token("admin@gimnasio.com"), "{malo")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("La hoja de asistencia de una sesión es solo para administrador y entrenador")
    void hojaDeAsistencia() throws Exception {
        String url = "/gimnasioAPI/asistencias/clases/{id}/sesiones/2026-12-01/asistencia";
        Long claseId = primeraClase(token("admin@gimnasio.com"));

        for (String correo : List.of("admin@gimnasio.com", "entrenador@gimnasio.com")) {
            mockMvc.perform(get(url, claseId).header("Authorization", token(correo))).andExpect(status().isOk());
        }
        mockMvc.perform(get(url, claseId).header("Authorization", token("miembro@gimnasio.com")))
                .andExpect(status().isForbidden());
    }
}
