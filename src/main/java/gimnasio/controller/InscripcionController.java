package gimnasio.controller;

import gimnasio.dto.InscripcionRequestDTO;
import gimnasio.dto.InscripcionResponseDTO;
import gimnasio.dto.InscripcionUpdateDTO;
import gimnasio.security.UsuarioAutenticado;
import gimnasio.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    // POST /gimnasioAPI/inscripciones - Inscribirse a una clase recurrente
    // El miembro se inscribe a sí mismo; el administrador indica el miembroId
    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> inscribirse(@RequestBody InscripcionRequestDTO requestDTO) {
        Long miembroAutenticado = UsuarioAutenticado.miembroId();
        if (miembroAutenticado != null) {
            requestDTO.setMiembroId(miembroAutenticado);
        }
        InscripcionResponseDTO respuesta = inscripcionService.inscribirMiembro(requestDTO, miembroAutenticado);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // GET /gimnasioAPI/inscripciones - Consultar inscripciones
    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> listarInscripciones(
            @RequestParam(required = false) Long miembroId,
            @RequestParam(required = false) Long claseId) {
        // El miembro solo ve sus propias inscripciones
        Long miembroAutenticado = UsuarioAutenticado.miembroId();
        if (miembroAutenticado != null) {
            miembroId = miembroAutenticado;
        }
        List<InscripcionResponseDTO> lista = inscripcionService.obtenerInscripciones(miembroId, claseId);
        return ResponseEntity.ok(lista);
    }

    // GET /gimnasioAPI/inscripciones/{id} - Obtener detalle de una inscripción
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        InscripcionResponseDTO detalle = inscripcionService.obtenerPorId(id);
        UsuarioAutenticado.verificarQueSeaSuyo(detalle.getMiembroId());
        return ResponseEntity.ok(detalle);
    }

    // PUT /gimnasioAPI/inscripciones/{id} - Cambiar de clase
    @PutMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> cambiarClase(
            @PathVariable Long id,
            @RequestBody InscripcionUpdateDTO updateDTO) {
        UsuarioAutenticado.verificarQueSeaSuyo(inscripcionService.obtenerPorId(id).getMiembroId());
        InscripcionResponseDTO respuesta = inscripcionService.cambiarClase(id, updateDTO, UsuarioAutenticado.miembroId());
        return ResponseEntity.ok(respuesta);
    }

    // DELETE /gimnasioAPI/inscripciones/{id} - Cancelar inscripción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarInscripcion(@PathVariable Long id) {
        UsuarioAutenticado.verificarQueSeaSuyo(inscripcionService.obtenerPorId(id).getMiembroId());
        inscripcionService.cancelarInscripcion(id, UsuarioAutenticado.miembroId());
        return ResponseEntity.noContent().build();
    }
}