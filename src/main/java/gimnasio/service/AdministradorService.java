package gimnasio.service;
import gimnasio.dto.AdministradorResponseDTO;
import gimnasio.dto.AdministradorUpdateDTO;
import gimnasio.model.Administrador;
import gimnasio.repositoy.AdministradorRepository;
import gimnasio.dto.AdministradorRequestDTO;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private AdministradorRepository administradorRepository;

    // Metodo para guardar administrador
    @Transactional
    public Administrador crearAdministrador(AdministradorRequestDTO dto){
        if (administradorRepository.findByCorreo(dto.getCorreo()).isPresent()){
            throw new IllegalStateException("El correo ya existe ya existe.");
        }
        Administrador administrador = new Administrador();
        administrador.setCorreo(dto.getCorreo());
        administrador.setCorreo(dto.getContrasenaHash());
        administrador.setCorreo(dto.getEstado());

        return administradorRepository.save(administrador);
    }

    // Metodo para listar todos los administradores
    @Transactional(readOnly = true)
    public List<Administrador> listarAdministradores() {
        return administradorRepository.findAll();
    }

    ///Elimnar un administrador
    @Transactional
    public void eliminarAdministrador(Long id) {
        if (!administradorRepository.existsById(id)) {
            throw new IllegalStateException("El administrador no existe.");
        }
        administradorRepository.deleteById(id);
    }

    //Buscar por id
    private Administrador buscar(Long id) {
            return administradorRepository.findById(id).orElseThrow(() -> new IllegalStateException("Administrador no encontrado con id " + id));
    }



    // Actualizar
    @Transactional
    public AdministradorResponseDTO actualizar(Long id, AdministradorUpdateDTO dto) {

        Administrador administrador = buscar(id);

        if (administradorRepository.existsByCorreoAndIdNot(
                dto.getCorreo(), id)) {

            throw new ConflictException(
                    "El correo ya está registrado en otra cuenta");
        }

        administrador.setCorreo(dto.getCorreo());
        administrador.setEstado(dto.getEstado());

        Administrador actualizado = administradorRepository.save(administrador);
        return AdministradorResponseDTO.builder()
                .correo(actualizado.getCorreo())
                .estado(actualizado.getEstado())
                .build();
    }


}


