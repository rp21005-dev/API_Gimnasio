package gimnasio.controller;

import gimnasio.dto.CategoriaRequestDTO;
import gimnasio.dto.CategoriaResponseDTO;
import gimnasio.dto.CategoriaUpdateDTO;
import gimnasio.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Agregar categoría
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crearCategoria(dto));
    }

    // Obtener todas las categorías
    @GetMapping
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaService.listarCategorias();
    }

    // Obtener una categoría por id
    @GetMapping("/{id}")
    public CategoriaResponseDTO obtenerPorId(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id);
    }

    // Actualizar categoría
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody CategoriaUpdateDTO dto) {
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }

    // Eliminar categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
