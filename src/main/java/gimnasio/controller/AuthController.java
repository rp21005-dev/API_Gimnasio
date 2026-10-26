package gimnasio.controller;
import gimnasio.dto.LoginRequestDTO;
import gimnasio.model.Usuario;
import gimnasio.repository.AdministradorRepository;
import gimnasio.repository.EntrenadorRepository;
import gimnasio.repository.MiembroRepository;
import gimnasio.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/gimnasioAPI/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdministradorRepository administradorRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final MiembroRepository miembroRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Inicio de sesión para administradores, entrenadores y miembros
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO dto) {
        String rol = buscarRol(dto);
        if (rol == null) {
            log.warn("Inicio de sesión fallido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        log.info("Inicio de sesión exitoso con rol {}", rol);
        return ResponseEntity.ok(Map.of("token", jwtUtils.generarToken(dto.getCorreo(), rol), "rol", rol));
    }

    // Busca el correo en cada tipo de usuario y devuelve su rol si la contraseña coincide
    private String buscarRol(LoginRequestDTO dto) {
        if (credencialesValidas(administradorRepository.findByCorreo(dto.getCorreo()), dto)) return "ADMINISTRADOR";
        if (credencialesValidas(entrenadorRepository.findByCorreo(dto.getCorreo()), dto)) return "ENTRENADOR";
        if (credencialesValidas(miembroRepository.findByCorreo(dto.getCorreo()), dto)) return "MIEMBRO";
        return null;
    }

    // Solo los usuarios ACTIVOS pueden iniciar sesión
    private boolean credencialesValidas(Optional<? extends Usuario> usuario, LoginRequestDTO dto) {
        return usuario.isPresent()
                && "ACTIVO".equals(usuario.get().getEstado())
                && passwordEncoder.matches(dto.getContrasena(), usuario.get().getContrasenaHash());
    }
}
