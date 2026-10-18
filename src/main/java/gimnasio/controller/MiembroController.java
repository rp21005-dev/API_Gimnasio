package gimnasio.controller;
import gimnasio.model.Miembro;
import gimnasio.service.MiembroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/miembros")
@RequiredArgsConstructor
public class MiembroController {


    private final MiembrosService miembrosService;

    //Agregar miembro
    @PostMapping
    public ResponseEntity<Miembro>crearMiembro(@RequestBody MiembroDTO miembroDTO){
        Miembro nuevoMiembro = miembrosService.crearMiembro(miembroDTO);
        return ResponseEntity.status(201).body(nuevoMiembro);
    }

    //Obtener todos los miembros
    @GetMapping()
    public List<Miembro> obtenerTodosLosMiembros(){
        return miembrosService.obtenerTodos();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MiembroResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MiembroUpdateDTO dto) {
        return ResponseEntity.ok(miembrosService.actualizar(id, dto));
    }

    //Eliminar miembro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        miembrosService.eliminar(id);
        return ResponseEntity.noContent().build();
    }






}
