package gimnasio.config;

import gimnasio.model.Administrador;
import gimnasio.model.Clase;
import gimnasio.model.Entrenador;
import gimnasio.model.Miembro;
import gimnasio.repository.AdministradorRepository;
import gimnasio.repository.ClaseRepository;
import gimnasio.repository.EntrenadorRepository;
import gimnasio.repository.MiembroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
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
            PasswordEncoder passwordEncoder) {

        return args -> {

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

            if (claseRepository.count() == 0) {
                Clase clase = new Clase();

                clase.setTipoClase("Spinning");
                clase.setDiasSemana(List.of("Lunes", "Miércoles", "Viernes"));
                clase.setHoraInicio(LocalTime.of(8, 0));
                clase.setHoraFin(LocalTime.of(9, 0));
                clase.setFechaInicio(LocalDate.of(2026, 11, 2));
                clase.setCupoMaximo(20);
                clase.setDescripcion("Clase de cardio en bicicleta fija.");
                clase.setEstado("ACTIVA");

                claseRepository.save(clase);
            }
        };
    }
}