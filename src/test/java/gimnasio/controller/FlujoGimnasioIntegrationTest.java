package gimnasio.controller;

import com.jayway.jsonpath.JsonPath;
import gimnasio.model.Entrenador;
import gimnasio.model.Miembro;
import gimnasio.repository.EntrenadorRepository;
import gimnasio.repository.MiembroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Prueba de integración de punta a punta: controlador -> servicio -> base de datos (H2 en memoria).
// Recorre el flujo principal del gimnasio: categoría -> sala -> clase -> inscripción -> asistencia,
// y verifica los códigos de error (400, 404, 409) que devuelve el ManejadorExcepciones.
// @Transactional deshace los cambios al terminar cada prueba.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = "ADMINISTRADOR")
class FlujoGimnasioIntegrationTest {

    private static final String HOY = LocalDate.now().toString();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntrenadorRepository entrenadorRepository;
    @Autowired
    private MiembroRepository miembroRepository;

    private Long entrenadorId;
    private Long miembroId;

    @BeforeEach
    void setUp() {
        Entrenador entrenador = new Entrenador("Laura", "Flujo", "7000-4444", "Yoga");
        entrenador.setCorreo("laura.flujo@ejemplo.com");
        entrenador.setContrasenaHash("no-se-usa");
        entrenador.setEstado("ACTIVO");
        entrenadorId = entrenadorRepository.save(entrenador).getId();

        Miembro miembro = new Miembro();
        miembro.setCorreo("pedro.flujo@ejemplo.com");
        miembro.setContrasenaHash("no-se-usa");
        miembro.setEstado("ACTIVO");
        miembro.setNombres("Pedro");
        miembro.setApellidos("Flujo");
        miembroId = miembroRepository.save(miembro).getId();
    }

    // ---------- Ayudantes ----------

    private ResultActions postJson(String url, String json) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private Long id(ResultActions resultado) throws Exception {
        String json = resultado.andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }

    private Long crearCategoria() throws Exception {
        return id(postJson("/gimnasioAPI/categorias", """
                {"nombre": "Categoria Flujo", "objetivoPrincipal": "Probar el flujo",
                 "tiposClase": ["Yoga Flujo", "Pilates Flujo"]}
                """).andExpect(status().isCreated()));
    }

    private Long crearSala(int capacidad) throws Exception {
        return id(postJson("/gimnasioAPI/salas", """
                {"codigo": "F-01", "nombre": "Sala Flujo", "ubicacion": "Tercer piso", "capacidadMaxima": %d}
                """.formatted(capacidad)).andExpect(status().isCreated()));
    }

    private String claseJson(Long categoriaId, Long salaId, String inicio, String fin, int cupo) {
        return """
                {"tipoClase": "Yoga Flujo", "diasSemana": ["lunes", "jueves"],
                 "horaInicio": "%s", "horaFin": "%s", "fechaInicio": "%s", "cupoMaximo": %d,
                 "categoriaId": %d, "salaId": %d, "entrenadorId": %d}
                """.formatted(inicio, fin, HOY, cupo, categoriaId, salaId, entrenadorId);
    }

    // ---------- Pruebas ----------

    @Test
    @DisplayName("Flujo completo: crear clase, inscribir al miembro, registrar y corregir asistencia")
    void flujoCompleto() throws Exception {
        Long categoriaId = crearCategoria();
        Long salaId = crearSala(10);

        Long claseId = id(postJson("/gimnasioAPI/clases", claseJson(categoriaId, salaId, "06:00", "07:00", 5))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.diasSemana[0]").value("Lunes"))
                .andExpect(jsonPath("$.entrenadorNombre").value("Laura Flujo")));

        mockMvc.perform(get("/gimnasioAPI/clases/{id}", claseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salaNombre").value("Sala Flujo"));

        Long inscripcionId = id(postJson("/gimnasioAPI/inscripciones", """
                {"claseId": %d, "miembroId": %d}
                """.formatted(claseId, miembroId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cupoDisponibleRestante").value(4)));

        // Inscribirse dos veces en la misma clase -> 409
        postJson("/gimnasioAPI/inscripciones", """
                {"claseId": %d, "miembroId": %d}
                """.formatted(claseId, miembroId))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/gimnasioAPI/inscripciones").param("miembroId", miembroId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/gimnasioAPI/inscripciones/{id}", inscripcionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoClase").value("Yoga Flujo"));

        Long asistenciaId = ((Number) JsonPath.read(postJson("/gimnasioAPI/asistencias", """
                {"claseId": %d, "fechaSesion": "%s",
                 "registros": [{"miembroId": %d, "estado": "ausente"}]}
                """.formatted(claseId, HOY, miembroId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ausentes").value(1))
                .andExpect(jsonPath("$.sinRegistro").value(0))
                .andReturn().getResponse().getContentAsString(), "$.registros[0].id")).longValue();

        mockMvc.perform(get("/gimnasioAPI/asistencias/clases/{claseId}/sesiones/{fecha}/asistencia", claseId, HOY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registros.length()").value(1));

        mockMvc.perform(put("/gimnasioAPI/asistencias/{id}", asistenciaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"presente\", \"observacion\": \"Llegó tarde\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PRESENTE"));

        mockMvc.perform(get("/gimnasioAPI/asistencias").param("claseId", claseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].observacion").value("Llegó tarde"));

        mockMvc.perform(delete("/gimnasioAPI/asistencias/{id}", asistenciaId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/gimnasioAPI/inscripciones/{id}", inscripcionId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/gimnasioAPI/clases/{id}", claseId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/gimnasioAPI/clases/{id}", claseId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Cambiar de clase una inscripción (PUT) actualiza la clase y el cupo")
    void cambiarDeClase() throws Exception {
        Long categoriaId = crearCategoria();
        Long salaId = crearSala(10);
        Long claseManana = id(postJson("/gimnasioAPI/clases", claseJson(categoriaId, salaId, "06:00", "07:00", 5)));
        Long claseNoche = id(postJson("/gimnasioAPI/clases", claseJson(categoriaId, salaId, "19:00", "20:00", 3)));

        Long inscripcionId = id(postJson("/gimnasioAPI/inscripciones", """
                {"claseId": %d, "miembroId": %d}
                """.formatted(claseManana, miembroId)));

        mockMvc.perform(put("/gimnasioAPI/inscripciones/{id}", inscripcionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevaClaseId\": %d}".formatted(claseNoche)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claseId").value(claseNoche));
    }

    @Test
    @DisplayName("Categorías: CRUD y nombre repetido (409)")
    void crudCategorias() throws Exception {
        Long id = crearCategoria();

        mockMvc.perform(put("/gimnasioAPI/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Categoria Flujo 2", "objetivoPrincipal": "Nuevo", "tiposClase": ["Step"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiposClase[0]").value("Step"));

        postJson("/gimnasioAPI/categorias", """
                {"nombre": "categoria flujo 2", "objetivoPrincipal": "x", "tiposClase": ["Zumba"]}
                """).andExpect(status().isConflict());

        // El catálogo inicial (CatalogoInicialLoader) también está cargado
        mockMvc.perform(get("/gimnasioAPI/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7));

        mockMvc.perform(delete("/gimnasioAPI/categorias/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/gimnasioAPI/categorias/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Salas: CRUD, validación (400) y código repetido (409)")
    void crudSalas() throws Exception {
        Long id = crearSala(12);

        mockMvc.perform(put("/gimnasioAPI/salas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo": "F-01", "nombre": "Sala Flujo Renovada", "capacidadMaxima": 18}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacidadMaxima").value(18));

        postJson("/gimnasioAPI/salas", """
                {"codigo": "f-01", "nombre": "Otra sala", "capacidadMaxima": 5}
                """).andExpect(status().isConflict());

        postJson("/gimnasioAPI/salas", """
                {"codigo": "", "nombre": "Sin código", "capacidadMaxima": 0}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.capacidadMaxima").exists());

        mockMvc.perform(delete("/gimnasioAPI/salas/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/gimnasioAPI/salas/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Clases: reglas de negocio responden 400 y 409")
    void reglasDeClase() throws Exception {
        Long categoriaId = crearCategoria();
        Long salaId = crearSala(10);

        // Cupo mayor a la capacidad de la sala -> 400
        postJson("/gimnasioAPI/clases", claseJson(categoriaId, salaId, "06:00", "07:00", 50))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        // Sala inexistente -> 404
        postJson("/gimnasioAPI/clases", claseJson(categoriaId, 999_999L, "06:00", "07:00", 5))
                .andExpect(status().isNotFound());

        // Mismo entrenador y horario que se cruza -> 409
        postJson("/gimnasioAPI/clases", claseJson(categoriaId, salaId, "06:00", "07:00", 5))
                .andExpect(status().isCreated());
        postJson("/gimnasioAPI/clases", claseJson(categoriaId, salaId, "06:30", "07:30", 5))
                .andExpect(status().isConflict());

        // Faltan campos obligatorios -> 400 con el detalle por campo
        postJson("/gimnasioAPI/clases", "{\"cupoMaximo\": 0, \"diasSemana\": []}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipoClase").exists())
                .andExpect(jsonPath("$.diasSemana").exists())
                .andExpect(jsonPath("$.cupoMaximo").exists())
                .andExpect(jsonPath("$.salaId").exists());
    }
}
