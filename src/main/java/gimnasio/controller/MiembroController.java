package gimnasio.controller;
import gimnasio.dto.MiembroUpdateDTO;
import gimnasio.dto.MiembroResponseDTO;
import gimnasio.dto.MiembroRequestDTO;
import gimnasio.service.MiembroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/miembros")
@RequiredArgsConstructor
public class MiembroController {


    private final MiembroService miembrosService;

    //Agregar miembro
    @PostMapping
    public ResponseEntity<MiembroResponseDTO>crearMiembro(@Valid @RequestBody MiembroRequestDTO miembroDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(miembrosService.crearMiembro(miembroDTO));
    }

    //Obtener todos los miembros
    @GetMapping()
    public List<MiembroResponseDTO> obtenerTodosLosMiembros(){
        return miembrosService.listarMiembro();
    }

    //Obtener un miembro por id
    @GetMapping("/{id}")
    public MiembroResponseDTO obtenerPorId(@PathVariable Long id){
        return miembrosService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MiembroResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MiembroUpdateDTO dto) {
        return ResponseEntity.ok(miembrosService.actualizar(id, dto));
    }

    //Eliminar miembro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        miembrosService.eliminarMiembro(id);
        return ResponseEntity.noContent().build();
    }






}
