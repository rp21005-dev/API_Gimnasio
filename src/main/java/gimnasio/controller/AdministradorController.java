package gimnasio.controller;
import gimnasio.dto.AdministradorRequestDTO;
import gimnasio.dto.AdministradorResponseDTO;
import gimnasio.dto.AdministradorUpdateDTO;

import gimnasio.service.AdministradorService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/administrador")
@RequiredArgsConstructor
public class AdministradorController {


    private final AdministradorService administradorService;

    //Agregar Administrador
    @PostMapping
    public ResponseEntity<AdministradorResponseDTO>crearAdministrador(@Valid @RequestBody AdministradorRequestDTO administradorDTO){
        return ResponseEntity.status(201).body(administradorService.crearAdministrador(administradorDTO));
    }

    //Obtener todos los administradores
    @GetMapping()
    public List<AdministradorResponseDTO> obtenerTodosLosAdministradores(){
        return administradorService.listarAdministradores();
    }

    //Obtener un administrador por id
    @GetMapping("/{id}")
    public AdministradorResponseDTO obtenerPorId(@PathVariable Long id){
        return administradorService.obtenerPorId(id);
    }

    //Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AdministradorUpdateDTO dto) {
        return ResponseEntity.ok(administradorService.actualizar(id, dto));
    }

    //Eliminar administrador
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        administradorService.eliminarAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}
