package gimnasio.controller;

import gimnasio.dto.AsistenciaRequestDTO;
import gimnasio.dto.AsistenciaResponseDTO;
import gimnasio.dto.AsistenciaUpdateDTO;
import gimnasio.dto.HojaAsistenciaResponseDTO;
import gimnasio.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    // POST /api/asistencias - Registrar asistencia de una sesión
    @PostMapping
    public ResponseEntity<HojaAsistenciaResponseDTO> registrarAsistencia(
            @RequestBody AsistenciaRequestDTO requestDTO,
            @RequestAttribute(value = "usuarioId", required = false) Long usuarioId) {
        HojaAsistenciaResponseDTO respuesta = asistenciaService.registrarAsistencia(requestDTO, usuarioId);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // GET /api/asistencias - Consultar historial de asistencias
    @GetMapping
    public ResponseEntity<List<AsistenciaResponseDTO>> listarAsistencias(
            @RequestParam(required = false) Long miembroId,
            @RequestParam(required = false) Long claseId) {
        List<AsistenciaResponseDTO> lista = asistenciaService.obtenerAsistencias(miembroId, claseId);
        return ResponseEntity.ok(lista);
    }

    // GET /api/asistencias/{id} - Consultar detalle de una asistencia
    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> obtenerPorId(@PathVariable Long id) {
        AsistenciaResponseDTO detalle = asistenciaService.obtenerPorId(id);
        return ResponseEntity.ok(detalle);
    }

    // GET /api/clases/{claseId}/sesiones/{fecha}/asistencia - Consultar hoja de asistencia de una sesión
    @GetMapping("/clases/{claseId}/sesiones/{fecha}/asistencia")
    public ResponseEntity<HojaAsistenciaResponseDTO> obtenerHojaAsistencia(
            @PathVariable Long claseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        HojaAsistenciaResponseDTO hoja = asistenciaService.obtenerHojaAsistencia(claseId, fecha);
        return ResponseEntity.ok(hoja);
    }

    // PUT /api/asistencias/{id} - Corregir asistencia
    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> corregirAsistencia(
            @PathVariable Long id,
            @RequestBody AsistenciaUpdateDTO updateDTO,
            @RequestAttribute(value = "usuarioId", required = false) Long usuarioId) {
        AsistenciaResponseDTO respuesta = asistenciaService.corregirAsistencia(id, updateDTO, usuarioId);
        return ResponseEntity.ok(respuesta);
    }

    // DELETE /api/asistencias/{id} - Eliminar registro de asistencia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsistencia(
            @PathVariable Long id,
            @RequestAttribute(value = "usuarioId", required = false) Long usuarioId) {
        asistenciaService.eliminarAsistencia(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
