package gimnasio.config;

import gimnasio.model.Administrador;
import gimnasio.model.Entrenador;
import gimnasio.model.Miembro;
import gimnasio.repositoy.AdministradorRepository;
import gimnasio.repositoy.EntrenadorRepository;
import gimnasio.repositoy.MiembroRepositoy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatos(
            AdministradorRepository administradorRepository,
            EntrenadorRepository entrenadorRepository,
            MiembroRepositoy miembroRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (administradorRepository.count() == 0) {
                Administrador administrador = new Administrador();

                administrador.setCorreo("admin@gimnasio.com");
                administrador.setContrasenaHash(
                        passwordEncoder.encode("123456")
                );
                administrador.setEstado("ACTIVO");

                administradorRepository.save(administrador);
            }

            if (entrenadorRepository.count() == 0) {
                Entrenador entrenador = new Entrenador();

                entrenador.setCorreo("entrenador@gimnasio.com");
                entrenador.setContrasenaHash(
                        passwordEncoder.encode("123456")
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
                        passwordEncoder.encode("123456")
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
        };
    }
}