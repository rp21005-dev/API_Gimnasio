package gimnasio.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// Convierte las excepciones en respuestas HTTP con el codigo correcto
@Slf4j
@RestControllerAdvice
public class ManejadorExcepciones {

    // 404: el recurso no existe
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    // 409: conflicto con una regla de negocio (ej. correo repetido)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> conflicto(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    // 409: el correo ya pertenece a otro usuario (administrador, entrenador o miembro)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> correoRepetido(DataIntegrityViolationException e) {
        log.warn("Intento de registrar un usuario con un correo que ya pertenece a otra cuenta");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "El correo ya está registrado en otra cuenta"));
    }

    // 400: datos invalidos segun las anotaciones de validacion de los DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {
        Map<String, String> errores = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}
