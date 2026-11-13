package gimnasio.config;

import gimnasio.model.Administrador;
import gimnasio.model.Categoria;
import gimnasio.model.Clase;
import gimnasio.model.Entrenador;
import gimnasio.model.Miembro;
import gimnasio.model.Sala;
import gimnasio.repository.AdministradorRepository;
import gimnasio.repository.CategoriaRepository;
import gimnasio.repository.ClaseRepository;
import gimnasio.repository.EntrenadorRepository;
import gimnasio.repository.MiembroRepository;
import gimnasio.repository.SalaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    // Contraseña de los usuarios de demostración (cumple la regla: 8 caracteres, letras y números)
    private static final String CONTRASENA_DEMO = "clave1234";

    @Bean
    CommandLineRunner cargarDatos(
            AdministradorRepository administradorRepository,
            EntrenadorRepository entrenadorRepository,
            MiembroRepository miembroRepository,
            ClaseRepository claseRepository,
            CategoriaRepository categoriaRepository,
            SalaRepository salaRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // ---------- Usuarios ----------
            if (administradorRepository.count() == 0) {
                Administrador administrador = new Administrador();

                administrador.setCorreo("admin@gimnasio.com");
                administrador.setContrasenaHash(
                        passwordEncoder.encode(CONTRASENA_DEMO)
                );
                administrador.setEstado("ACTIVO");

                administradorRepository.save(administrador);
            }

            if (entrenadorRepository.count() == 0) {
                Entrenador entrenador = new Entrenador();

                entrenador.setCorreo("entrenador@gimnasio.com");
                entrenador.setContrasenaHash(
                        passwordEncoder.encode(CONTRASENA_DEMO)
                );
                entrenador.setEstado("ACTIVO");

                entrenador.setNombres("Carlos");
                entrenador.setApellidos("Martínez");
                entrenador.setTelefono("7000-1111");
                entrenador.setEspecialidad("Musculación");

                entrenadorRepository.save(entrenador);
            }

            if (miembroRepository.count() == 0) {
                Miembro miembro = new Miembro();

                miembro.setCorreo("miembro@gimnasio.com");
                miembro.setContrasenaHash(
                        passwordEncoder.encode(CONTRASENA_DEMO)
                );
                miembro.setEstado("ACTIVO");

                miembro.setNombres("Juan");
                miembro.setApellidos("Pérez");
                miembro.setDocumentoIdentidad("12345678-9");
                miembro.setTelefono("7000-2222");
                miembro.setFechaNacimiento(
                        LocalDate.of(2000, 5, 15)
                );
                miembro.setFechaRegistro(
                        LocalDate.now()
                );

                miembroRepository.save(miembro);
            }

            // ---------- Catálogo de categorías y tipos de clase ----------
            if (categoriaRepository.count() == 0) {
                categoriaRepository.saveAll(List.of(
                        categoria("Cardiovasculares (Cardio)",
                                "Resistencia aeróbica, quema calórica y salud cardiovascular.",
                                "Spinning / Indoor cycling", "Zumba", "Step", "Aeróbicos"),
                        categoria("Fuerza y Resistencia Muscular",
                                "Hipertrofia ligera, tono muscular y resistencia a la fatiga con cargas.",
                                "BodyPump", "GAP (Glúteos, Abdomen, Piernas)", "Power Bar", "Circuitos de pesas"),
                        categoria("Alta Intensidad (HIIT / Funcional)",
                                "Potencia, aceleración metabólica y capacidad anaeróbica en intervalos cortos.",
                                "Tabata", "CrossFit / Cross Training", "Boot Camp", "HIIT en cinta o funcional"),
                        categoria("Mente y Cuerpo (Bajo Impacto)",
                                "Flexibilidad, alineación postural, respiración y fuerza isométrica del core.",
                                "Pilates (Mat o Reformer)", "Yoga (Hatha, Vinyasa)", "BodyBalance", "Estiramiento / Movilidad"),
                        categoria("Combate y Artes Marciales",
                                "Coordinación, agilidad, reflejos y cardio de media-alta intensidad.",
                                "BodyCombat", "Kickboxing fitness", "Boxeo recreativo", "FitBoxing"),
                        categoria("Acuáticas (Aquafitness)",
                                "Resistencia muscular y cardiovascular sin impacto articular.",
                                "Aquagym", "AquaZumba", "AquaSpinning")
                ));
            }

            // ---------- Salas ----------
            if (salaRepository.count() == 0) {
                salaRepository.saveAll(List.of(
                        sala("S-01", "Sala de Ciclismo", "Primer piso", 25, "Bicicletas fijas para spinning."),
                        sala("S-02", "Sala Multiuso", "Primer piso", 30, "Zumba, step, aeróbicos y funcional."),
                        sala("S-03", "Sala Mente y Cuerpo", "Segundo piso", 20, "Yoga, pilates y estiramiento.")
                ));
            }

            // ---------- Clase de ejemplo (con categoría, sala y entrenador) ----------
            if (claseRepository.count() == 0) {
                Categoria categoria = categoriaRepository.findAll().stream()
                        .filter(c -> c.getNombre().equals("Cardiovasculares (Cardio)"))
                        .findFirst().orElse(null);
                Sala sala = salaRepository.findAll().stream()
                        .filter(s -> s.getCodigo().equals("S-01"))
                        .findFirst().orElse(null);
                Entrenador entrenador = entrenadorRepository.findByCorreo("entrenador@gimnasio.com").orElse(null);

                // Solo se crea si existen las tres relaciones
                if (categoria != null && sala != null && entrenador != null) {
                    Clase clase = new Clase();

                    // El tipo debe ser uno de los tipos de la categoría
                    clase.setTipoClase("Spinning / Indoor cycling");
                    clase.setDiasSemana(List.of("Lunes", "Miércoles", "Viernes"));
                    clase.setHoraInicio(LocalTime.of(8, 0));
                    clase.setHoraFin(LocalTime.of(9, 0));
                    clase.setFechaInicio(LocalDate.of(2026, 11, 2));
                    clase.setCupoMaximo(20); // no supera la capacidad de la sala (25)
                    clase.setDescripcion("Clase de cardio en bicicleta fija.");
                    clase.setEstado("ACTIVA");

                    clase.setCategoria(categoria);
                    clase.setSala(sala);
                    clase.setEntrenador(entrenador);

                    claseRepository.save(clase);
                }
            }
        };
    }

    // Crea una categoría con su lista de tipos de clase
    private Categoria categoria(String nombre, String objetivo, String... tipos) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setObjetivoPrincipal(objetivo);
        c.setTiposClase(new ArrayList<>(List.of(tipos)));
        return c;
    }

    // Crea una sala
    private Sala sala(String codigo, String nombre, String ubicacion, int capacidad, String descripcion) {
        Sala s = new Sala();
        s.setCodigo(codigo);
        s.setNombre(nombre);
        s.setUbicacion(ubicacion);
        s.setCapacidadMaxima(capacidad);
        s.setDescripcion(descripcion);
        return s;
    }
}