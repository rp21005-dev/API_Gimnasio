package gimnasio.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Prueba de integración del inicio de sesión con los usuarios de demostración (DataInitializer)
// y del filtro JWT: el token generado debe dar acceso a un endpoint protegido.
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String login(String correo, String contrasena) {
        return """
                {"correo": "%s", "contrasena": "%s"}
                """.formatted(correo, contrasena);
    }

    @Test
    @DisplayName("Login correcto devuelve 200 con token y rol, y el token permite usar la API")
    void login_TokenValidoDaAcceso() throws Exception {
        String respuesta = mockMvc.perform(post("/gimnasioAPI/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login("admin@gimnasio.com", "clave1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(respuesta, "$.token");

        mockMvc.perform(get("/gimnasioAPI/salas").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Cada tipo de usuario inicia sesión con su rol")
    void login_Roles() throws Exception {
        mockMvc.perform(post("/gimnasioAPI/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login("entrenador@gimnasio.com", "clave1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ENTRENADOR"));

        mockMvc.perform(post("/gimnasioAPI/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login("miembro@gimnasio.com", "clave1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("MIEMBRO"));
    }

    @Test
    @DisplayName("Contraseña incorrecta devuelve 401 Unauthorized")
    void login_ContrasenaIncorrecta() throws Exception {
        mockMvc.perform(post("/gimnasioAPI/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login("admin@gimnasio.com", "incorrecta1")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("Correo con formato inválido devuelve 400 Bad Request")
    void login_DatosInvalidos() throws Exception {
        mockMvc.perform(post("/gimnasioAPI/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login("no-es-correo", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.correo").exists())
                .andExpect(jsonPath("$.contrasena").exists());
    }

    @Test
    @DisplayName("La página web es pública, pero los datos siguen pidiendo token")
    void paginaWebPublica() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"login\"")));

        mockMvc.perform(get("/gimnasioAPI/clases"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un token falso no da acceso (401)")
    void tokenInvalido() throws Exception {
        mockMvc.perform(get("/gimnasioAPI/salas").header("Authorization", "Bearer token.falso.123"))
                .andExpect(status().isUnauthorized());
    }
}
