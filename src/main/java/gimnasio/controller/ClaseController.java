package gimnasio.controller;

import gimnasio.dto.ClaseRequestDTO;
import gimnasio.dto.ClaseResponseDTO;
import gimnasio.dto.ClaseUpdateDTO;
import gimnasio.service.ClaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/clases")
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseService claseService;

    // Agregar clase
    @PostMapping
    public ResponseEntity<ClaseResponseDTO> crearClase(@Valid @RequestBody ClaseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(claseService.crearClase(dto));
    }

    // Obtener todas las clases
    @GetMapping
    public List<ClaseResponseDTO> obtenerTodas() {
        return claseService.listarClases();
    }

    // Obtener una clase por id
    @GetMapping("/{id}")
    public ClaseResponseDTO obtenerPorId(@PathVariable Long id) {
        return claseService.obtenerPorId(id);
    }

    // Actualizar clase
    @PutMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ClaseUpdateDTO dto) {
        return ResponseEntity.ok(claseService.actualizar(id, dto));
    }

    // Eliminar clase
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        claseService.eliminarClase(id);
        return ResponseEntity.noContent().build();
    }
}
