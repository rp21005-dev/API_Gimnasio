package gimnasio.controller;

import gimnasio.dto.SalaRequestDTO;
import gimnasio.dto.SalaResponseDTO;
import gimnasio.dto.SalaUpdateDTO;
import gimnasio.service.SalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    // Agregar sala
    @PostMapping
    public ResponseEntity<SalaResponseDTO> crearSala(@Valid @RequestBody SalaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.crearSala(dto));
    }

    // Obtener todas las salas
    @GetMapping
    public List<SalaResponseDTO> obtenerTodas() {
        return salaService.listarSalas();
    }

    // Obtener una sala por id
    @GetMapping("/{id}")
    public SalaResponseDTO obtenerPorId(@PathVariable Long id) {
        return salaService.obtenerPorId(id);
    }

    // Actualizar sala
    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody SalaUpdateDTO dto) {
        return ResponseEntity.ok(salaService.actualizar(id, dto));
    }

    // Eliminar sala
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        salaService.eliminarSala(id);
        return ResponseEntity.noContent().build();
    }
}
