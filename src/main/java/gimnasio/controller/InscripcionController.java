package gimnasio.controller;

import gimnasio.dto.InscripcionRequestDTO;
import gimnasio.dto.InscripcionResponseDTO;
import gimnasio.dto.InscripcionUpdateDTO;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    // POST /api/inscripciones - Inscribirse a una clase recurrente
    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> inscribirse(
            @RequestBody InscripcionRequestDTO requestDTO,
            @RequestAttribute(value = "usuarioId", required = false) Long usuarioId) {
        InscripcionResponseDTO respuesta = inscripcionService.inscribirMiembro(requestDTO, usuarioId);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // GET /api/inscripciones - Consultar inscripciones
    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> listarInscripciones(
            @RequestParam(required = false) Long miembroId,
            @RequestParam(required = false) Long claseId) {
        List<InscripcionResponseDTO> lista = inscripcionService.obtenerInscripciones(miembroId, claseId);
        return ResponseEntity.ok(lista);
    }

    // GET /api/inscripciones/{id} - Obtener detalle de una inscripción
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        InscripcionResponseDTO detalle = inscripcionService.obtenerPorId(id);
        return ResponseEntity.ok(detalle);
    }

    // PUT /api/inscripciones/{id} - Cambiar de clase
    @PutMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> cambiarClase(
            @PathVariable Long id,
            @RequestBody InscripcionUpdateDTO updateDTO,
            @RequestAttribute(value = "usuarioId", required = false) Long usuarioId) {
        InscripcionResponseDTO respuesta = inscripcionService.cambiarClase(id, updateDTO, usuarioId);
        return ResponseEntity.ok(respuesta);
    }

    // DELETE /api/inscripciones/{id} - Cancelar inscripción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarInscripcion(
            @PathVariable Long id,
            @RequestAttribute(value = "usuarioId", required = false) Long usuarioId) {
        inscripcionService.cancelarInscripcion(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}