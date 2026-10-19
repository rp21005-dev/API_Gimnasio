package gimnasio.controller;
import gimnasio.dto.EntrenadorRequestDTO;
import gimnasio.dto.EntrenadorResponseDTO;
import gimnasio.dto.EntrenadorUpdateDTO;
import gimnasio.model.Entrenador;
import gimnasio.service.EntrenadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Entrenador> obtenerTodosLosEntrenadores(){

        return entrenadorService.listarEntrenador();
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




