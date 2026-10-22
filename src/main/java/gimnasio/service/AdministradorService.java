package gimnasio.service;
import gimnasio.dto.AdministradorResponseDTO;
import gimnasio.dto.AdministradorUpdateDTO;
import gimnasio.model.Administrador;
import gimnasio.repositoy.AdministradorRepository;
import gimnasio.dto.AdministradorRequestDTO;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    // Metodo para guardar administrador
    @Transactional
    public AdministradorResponseDTO crearAdministrador(AdministradorRequestDTO dto){
        if (administradorRepository.findByCorreo(dto.getCorreo()).isPresent()){
            throw new ConflictException("El correo ya existe.");
        }
        Administrador administrador = new Administrador();
        administrador.setCorreo(dto.getCorreo());
        administrador.setContrasenaHash(passwordEncoder.encode(dto.getContrasenaHash()));
        administrador.setEstado(dto.getEstado());

        return toResponse(administradorRepository.save(administrador));
    }

    // Metodo solo para devolver solo algunos atributos y no todos como la contraseña
    private AdministradorResponseDTO toResponse(Administrador administrador){
        return AdministradorResponseDTO.builder()
                .id(administrador.getId())
                .correo(administrador.getCorreo())
                .estado(administrador.getEstado())
                .build();
    }

    // Metodo para listar todos los administradores
    @Transactional(readOnly = true)
    public List<AdministradorResponseDTO> listarAdministradores() {
        return administradorRepository.findAll().stream().map(this::toResponse).toList();
    }

    // Obtener un administrador por id
    @Transactional(readOnly = true)
    public AdministradorResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    ///Elimnar un administrador
    @Transactional
    public void eliminarAdministrador(Long id) {
        if (!administradorRepository.existsById(id)) {
            throw new NoSuchElementException("El administrador no existe.");
        }
        administradorRepository.deleteById(id);
    }

    //Buscar por id
    private Administrador buscar(Long id) {
            return administradorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Administrador no encontrado con id " + id));
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

        return toResponse(administradorRepository.save(administrador));
    }


}
