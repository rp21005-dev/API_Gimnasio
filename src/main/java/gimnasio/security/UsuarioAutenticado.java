package gimnasio.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

// Datos del usuario que hace la petición, tomados del token JWT (ver JwtAuthenticationFilter)
public final class UsuarioAutenticado {

    private UsuarioAutenticado() {
    }

    // ID del miembro que hace la petición, o null si la hace un administrador o un entrenador
    public static Long miembroId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esMiembro = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MIEMBRO".equals(a.getAuthority()));
        if (!esMiembro) {
            return null;
        }

        RequestAttributes peticion = RequestContextHolder.getRequestAttributes();
        Object id = peticion == null ? null : peticion.getAttribute("usuarioId", RequestAttributes.SCOPE_REQUEST);
        if (id == null) {
            // Token anterior a que se guardara el ID: hay que iniciar sesión de nuevo
            throw new AccessDeniedException("Vuelve a iniciar sesión para continuar");
        }
        return (Long) id;
    }

    // Un miembro solo puede ver o modificar sus propios registros
    public static void verificarQueSeaSuyo(Long miembroIdDelRegistro) {
        Long propio = miembroId();
        if (propio != null && !Objects.equals(propio, miembroIdDelRegistro)) {
            throw new AccessDeniedException("Solo puedes consultar o modificar tus propios registros");
        }
    }
}
