package gimnasio.controller;
import gimnasio.dto.EntrenadorRequestDTO;
import gimnasio.dto.EntrenadorResponseDTO;
import gimnasio.dto.EntrenadorUpdateDTO;
import gimnasio.service.EntrenadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/entrenador")
@RequiredArgsConstructor
public class EntrenadorController {


    private final EntrenadorService entrenadorService;

    //Agregar Entrenador
    @PostMapping
    public ResponseEntity<EntrenadorResponseDTO>crearEntrenador(@Valid @RequestBody EntrenadorRequestDTO entrenadorDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(entrenadorService.crearEntrenador(entrenadorDTO));
    }

    //Obtener todos los entrenadores
    @GetMapping()
    public List<EntrenadorResponseDTO> obtenerTodosLosEntrenadores(){
        return entrenadorService.listarEntrenador();
    }

    //Obtener un entrenador por id
    @GetMapping("/{id}")
    public EntrenadorResponseDTO obtenerPorId(@PathVariable Long id){
        return entrenadorService.obtenerPorId(id);
    }

    //Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<EntrenadorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EntrenadorUpdateDTO dto) {
        return ResponseEntity.ok(entrenadorService.actualizar(id, dto));
    }

    //Eliminar entrenador
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entrenadorService.eliminarEntrenador(id);
        return ResponseEntity.noContent().build();
    }
}




