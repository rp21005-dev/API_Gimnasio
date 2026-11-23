package gimnasio.config;

import gimnasio.model.Administrador;
import gimnasio.model.Categoria;
import gimnasio.model.Clase;
import gimnasio.model.Entrenador;
import gimnasio.model.Inscripcion;
import gimnasio.model.Miembro;
import gimnasio.model.Sala;
import gimnasio.repository.AdministradorRepository;
import gimnasio.repository.CategoriaRepository;
import gimnasio.repository.ClaseRepository;
import gimnasio.repository.EntrenadorRepository;
import gimnasio.repository.MiembroRepository;
import gimnasio.repository.InscripcionRepository;
import gimnasio.repository.SalaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Se desactiva con DATOS_DEMO=false (por ejemplo, en producción)
@Configuration
@ConditionalOnProperty(name = "gimnasio.datos-demo", havingValue = "true", matchIfMissing = true)
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
            InscripcionRepository inscripcionRepository,
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

            // ---------- Más usuarios de demostración (solo si no existen) ----------
            if (entrenadorRepository.findByCorreo("entrenadora@gimnasio.com").isEmpty()) {
                Entrenador entrenadora = new Entrenador();
                entrenadora.setCorreo("entrenadora@gimnasio.com");
                entrenadora.setContrasenaHash(passwordEncoder.encode(CONTRASENA_DEMO));
                entrenadora.setEstado("ACTIVO");
                entrenadora.setNombres("Ana");
                entrenadora.setApellidos("López");
                entrenadora.setTelefono("7000-3333");
                entrenadora.setEspecialidad("Yoga y pilates");
                entrenadorRepository.save(entrenadora);
            }
            miembroDemo(miembroRepository, passwordEncoder, "maria@gimnasio.com", "María", "Hernández", "23456789-0", LocalDate.of(1998, 8, 21));
            miembroDemo(miembroRepository, passwordEncoder, "pedro@gimnasio.com", "Pedro", "Ramírez", "34567890-1", LocalDate.of(2002, 2, 3));

            // ---------- Horario de clases (sin choques de sala ni de entrenador) ----------
            Entrenador carlos = entrenadorRepository.findByCorreo("entrenador@gimnasio.com").orElse(null);
            Entrenador ana = entrenadorRepository.findByCorreo("entrenadora@gimnasio.com").orElse(null);
            if (carlos != null && ana != null && categoriaRepository.count() > 0 && salaRepository.count() > 0) {
                ClaseDemo[] horario = {
                        new ClaseDemo("Spinning / Indoor cycling", "Cardiovasculares (Cardio)", "S-01", carlos,
                                List.of("Lunes", "Miércoles", "Viernes"), 8, 0, 9, 0, 20, "Clase de cardio en bicicleta fija."),
                        new ClaseDemo("Zumba", "Cardiovasculares (Cardio)", "S-02", carlos,
                                List.of("Martes", "Jueves"), 18, 0, 19, 0, 25, "Baile aeróbico con ritmos latinos."),
                        new ClaseDemo("BodyPump", "Fuerza y Resistencia Muscular", "S-02", carlos,
                                List.of("Lunes", "Miércoles", "Viernes"), 18, 0, 19, 0, 20, "Trabajo con barra y discos para todo el cuerpo."),
                        new ClaseDemo("Yoga (Hatha, Vinyasa)", "Mente y Cuerpo (Bajo Impacto)", "S-03", ana,
                                List.of("Lunes", "Miércoles"), 7, 0, 8, 0, 15, "Posturas, respiración y relajación."),
                        new ClaseDemo("Pilates (Mat o Reformer)", "Mente y Cuerpo (Bajo Impacto)", "S-03", ana,
                                List.of("Martes", "Jueves"), 7, 0, 8, 0, 12, "Fortalecimiento del core y postura."),
                        new ClaseDemo("Kickboxing fitness", "Combate y Artes Marciales", "S-02", ana,
                                List.of("Sábado"), 10, 0, 11, 30, 20, "Golpes y patadas al ritmo de la música."),
                };
                for (ClaseDemo demo : horario) {
                    if (claseRepository.existsByTipoClase(demo.tipo())) continue;
                    Categoria categoria = categoriaRepository.findAll().stream()
                            .filter(c -> c.getNombre().equals(demo.categoria())).findFirst().orElse(null);
                    Sala sala = salaRepository.findAll().stream()
                            .filter(x -> x.getCodigo().equals(demo.sala())).findFirst().orElse(null);
                    if (categoria == null || sala == null) continue;

                    Clase clase = new Clase();
                    clase.setTipoClase(demo.tipo()); // debe ser uno de los tipos de la categoría
                    clase.setDiasSemana(new ArrayList<>(demo.dias()));
                    clase.setHoraInicio(LocalTime.of(demo.horaInicio(), demo.minutoInicio()));
                    clase.setHoraFin(LocalTime.of(demo.horaFin(), demo.minutoFin()));
                    clase.setFechaInicio(LocalDate.of(2026, 11, 2));
                    clase.setCupoMaximo(demo.cupo()); // no supera la capacidad de la sala
                    clase.setDescripcion(demo.descripcion());
                    clase.setEstado("ACTIVA");
                    clase.setCategoria(categoria);
                    clase.setSala(sala);
                    clase.setEntrenador(demo.entrenador());
                    claseRepository.save(clase);
                }
            }

            // ---------- Inscripciones de ejemplo (sin traslapes por miembro) ----------
            if (inscripcionRepository.count() == 0) {
                inscribir(inscripcionRepository, miembroRepository, claseRepository, "miembro@gimnasio.com", "Yoga (Hatha, Vinyasa)");
                inscribir(inscripcionRepository, miembroRepository, claseRepository, "miembro@gimnasio.com", "Zumba");
                inscribir(inscripcionRepository, miembroRepository, claseRepository, "maria@gimnasio.com", "Pilates (Mat o Reformer)");
                inscribir(inscripcionRepository, miembroRepository, claseRepository, "maria@gimnasio.com", "Spinning / Indoor cycling");
                inscribir(inscripcionRepository, miembroRepository, claseRepository, "pedro@gimnasio.com", "BodyPump");
                inscribir(inscripcionRepository, miembroRepository, claseRepository, "pedro@gimnasio.com", "Kickboxing fitness");
            }
        };
    }

    // Datos de una clase del horario de demostración
    private record ClaseDemo(String tipo, String categoria, String sala, Entrenador entrenador, List<String> dias,
                             int horaInicio, int minutoInicio, int horaFin, int minutoFin, int cupo, String descripcion) {
    }

    // Crea un miembro de demostración si su correo no existe
    private void miembroDemo(MiembroRepository repo, PasswordEncoder encoder, String correo, String nombres,
                             String apellidos, String documento, LocalDate nacimiento) {
        if (repo.findByCorreo(correo).isPresent()) return;
        Miembro m = new Miembro();
        m.setCorreo(correo);
        m.setContrasenaHash(encoder.encode(CONTRASENA_DEMO));
        m.setEstado("ACTIVO");
        m.setNombres(nombres);
        m.setApellidos(apellidos);
        m.setDocumentoIdentidad(documento);
        m.setTelefono("7000-4444");
        m.setFechaNacimiento(nacimiento);
        m.setFechaRegistro(LocalDate.now());
        repo.save(m);
    }

    // Inscribe a un miembro de demostración en la clase con ese tipo
    private void inscribir(InscripcionRepository inscripciones, MiembroRepository miembros,
                           ClaseRepository clases, String correo, String tipoClase) {
        Miembro miembro = miembros.findByCorreo(correo).orElse(null);
        Clase clase = clases.findAll().stream()
                .filter(c -> c.getTipoClase().equals(tipoClase)).findFirst().orElse(null);
        if (miembro != null && clase != null) {
            inscripciones.save(new Inscripcion(miembro, clase, LocalDate.of(2026, 11, 2), "activa"));
        }
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