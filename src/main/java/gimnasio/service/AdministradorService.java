package gimnasio.service;
import gimnasio.dto.AdministradorResponseDTO;
import gimnasio.dto.AdministradorUpdateDTO;
import gimnasio.model.Administrador;
import gimnasio.repositoy.AdministradorRepository;
import gimnasio.dto.AdministradorRequestDTO;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j // Lombok crea el logger: private static final Logger log
@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    // Metodo para guardar administrador
    @Transactional
    public AdministradorResponseDTO crearAdministrador(AdministradorRequestDTO dto){
        if (administradorRepository.findByCorreo(dto.getCorreo()).isPresent()){
            log.warn("Intento de registrar un administrador con un correo ya existente");
            throw new ConflictException("El correo ya existe.");
        }
        Administrador administrador = new Administrador();
        administrador.setCorreo(dto.getCorreo());
        administrador.setContrasenaHash(passwordEncoder.encode(dto.getContrasenaHash()));
        administrador.setEstado(dto.getEstado());

        Administrador guardado = administradorRepository.save(administrador);
        log.info("Administrador creado con id {}", guardado.getId());
        return toResponse(guardado);
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
    // La primera vez consulta la BD; las siguientes con el mismo id se leen de memoria
    @Cacheable(value = "administradores", key = "#id")
    @Transactional(readOnly = true)
    public AdministradorResponseDTO obtenerPorId(Long id) {
        // Este mensaje solo aparece cuando el dato NO está en caché
        log.debug("Consultando administrador con id {} en la base de datos", id);
        return toResponse(buscar(id));
    }

    ///Elimnar un administrador
    @CacheEvict(value = "administradores", key = "#id")
    @Transactional
    public void eliminarAdministrador(Long id) {
        if (!administradorRepository.existsById(id)) {
            log.warn("Se intentó eliminar un administrador inexistente con id {}", id);
            throw new NoSuchElementException("El administrador no existe.");
        }
        administradorRepository.deleteById(id);
        log.info("Administrador con id {} eliminado", id);
    }

    //Buscar por id
    private Administrador buscar(Long id) {
            return administradorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Administrador no encontrado con id " + id));
    }



    // Actualizar
    // Al actualizar se borra del caché para no devolver datos desactualizados
    @CacheEvict(value = "administradores", key = "#id")
    @Transactional
    public AdministradorResponseDTO actualizar(Long id, AdministradorUpdateDTO dto) {

        Administrador administrador = buscar(id);

        if (administradorRepository.existsByCorreoAndIdNot(
                dto.getCorreo(), id)) {

            log.warn("No se pudo actualizar el administrador con id {}: correo repetido", id);
            throw new ConflictException(
                    "El correo ya está registrado en otra cuenta");
        }

        administrador.setCorreo(dto.getCorreo());
        administrador.setEstado(dto.getEstado());

        log.info("Administrador con id {} actualizado", id);
        return toResponse(administradorRepository.save(administrador));
    }

}
