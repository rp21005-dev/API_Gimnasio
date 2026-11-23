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
        Sesion sesion = buscarUsuario(dto);
        if (sesion == null) {
            log.warn("Inicio de sesión fallido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        log.info("Inicio de sesión exitoso con rol {}", sesion.rol());
        String token = jwtUtils.generarToken(sesion.id(), dto.getCorreo(), sesion.rol());
        return ResponseEntity.ok(Map.of("token", token, "rol", sesion.rol(), "id", sesion.id().toString()));
    }

    private record Sesion(Long id, String rol) {}

    // Busca el correo en cada tipo de usuario y devuelve su ID y rol si la contraseña coincide
    private Sesion buscarUsuario(LoginRequestDTO dto) {
        Sesion sesion = validar(administradorRepository.findByCorreo(dto.getCorreo()), dto, "ADMINISTRADOR");
        if (sesion == null) sesion = validar(entrenadorRepository.findByCorreo(dto.getCorreo()), dto, "ENTRENADOR");
        if (sesion == null) sesion = validar(miembroRepository.findByCorreo(dto.getCorreo()), dto, "MIEMBRO");
        return sesion;
    }

    // Solo los usuarios ACTIVOS pueden iniciar sesión
    private Sesion validar(Optional<? extends Usuario> usuario, LoginRequestDTO dto, String rol) {
        boolean valido = usuario.isPresent()
                && "ACTIVO".equals(usuario.get().getEstado())
                && passwordEncoder.matches(dto.getContrasena(), usuario.get().getContrasenaHash());
        return valido ? new Sesion(usuario.get().getId(), rol) : null;
    }
}
