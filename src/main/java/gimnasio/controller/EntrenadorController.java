package gimnasio.controller;
import gimnasio.dto.EntrenadorRequestDTO;
import gimnasio.dto.EntrenadorResponseDTO;
import gimnasio.dto.EntrenadorUpdateDTO;
import gimnasio.model.Entrenador;
import gimnasio.service.EntrenadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Entrenador>crearEntrenador(@RequestBody EntrenadorRequestDTO entrenadorDTO){
        Entrenador nuevoEntrenador = entrenadorService.crearEntrenador(entrenadorDTO);
        return ResponseEntity.status(201).body(nuevoEntrenador);
    }

    //Obtener todos los entrenadores
    @GetMapping()
    public List<Entrenador> obtenerTodosLosEntrenadores(){
        return entrenadorService.obtenerTodos();
    }

    //Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<EntrenadorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EntrenadorUpdateDTO dto) {
        return ResponseEntity.ok(entrenadorService.actualizar(id, dto));
    }

    //Eliminar entrenador
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entrenadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}




