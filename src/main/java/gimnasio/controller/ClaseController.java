package gimnasio.controller;

import gimnasio.dto.ClaseRequestDTO;
import gimnasio.dto.ClaseResponseDTO;
import gimnasio.dto.ClaseUpdateDTO;
import gimnasio.service.ClaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    @Autowired
    private ClaseService claseService;

    @GetMapping
    public ResponseEntity<List<ClaseResponseDTO>> listarClases() {
        return ResponseEntity.ok(claseService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> obtenerClasePorId(@PathVariable int id) {
        return claseService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClaseResponseDTO> crearClase(@RequestBody ClaseRequestDTO requestDTO) {
        ClaseResponseDTO creada = claseService.guardar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> actualizarClase(@PathVariable int id, @RequestBody ClaseUpdateDTO updateDTO) {
        return claseService.actualizar(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarClase(@PathVariable int id) {
        if (claseService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}