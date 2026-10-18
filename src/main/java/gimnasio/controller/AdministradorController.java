package gimnasio.controller;
import gimnasio.dto.AdministradorRequestDTO;
import gimnasio.dto.AdministradorResponseDTO;
import gimnasio.dto.AdministradorUpdateDTO;

import gimnasio.model.Administrador;
import gimnasio.service.AdministradorService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gimnasioAPI/administrador")
@RequiredArgsConstructor
public class AdministradorController {


    private final AdministradorService administradorService;

    //Agregar Entrenador
    @PostMapping
    public ResponseEntity<Administrador>crearAdministrador(@RequestBody AdministradorRequestDTO administradorDTO){
        Administrador nuevoAdministrador = administradorService.crearAdministrador(administradorDTO);
        return ResponseEntity.status(201).body(nuevoAdministrador);
    }

    //Obtener todos los administradores
    @GetMapping()
    public List<Administrador> obtenerTodosLosAdministradores(){
        return administradorService.listarAdministradores();
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
