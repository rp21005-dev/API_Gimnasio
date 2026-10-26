package gimnasio.service;
import gimnasio.dto.*;
import gimnasio.model.Miembro;
import gimnasio.repository.UsuarioRepository;
import gimnasio.repository.MiembroRepository;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j // Lombok crea el logger: private static final Logger log
@Service
@RequiredArgsConstructor
public class MiembroService {

    private final MiembroRepository miembroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public MiembroResponseDTO crearMiembro(MiembroRequestDTO dto){
        if (usuarioRepository.existsByCorreo(dto.getCorreo())){
            log.warn("Intento de registrar un miembro con un correo ya existente");
            throw new ConflictException("El correo ya existe.");
        }
        Miembro miembro = new Miembro();
        miembro.setNombres(dto.getNombres());
        miembro.setApellidos(dto.getApellidos());
        miembro.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        miembro.setCorreo(dto.getCorreo());
        miembro.setTelefono(dto.getTelefono());
        miembro.setFechaNacimiento(dto.getFechaNacimiento());
        miembro.setContrasenaHash(passwordEncoder.encode(dto.getContrasenaHash()));
        miembro.setFechaRegistro(LocalDate.now());
        miembro.setEstado("ACTIVO");
        Miembro guardado = miembroRepository.save(miembro);
        log.info("Miembro creado con id {}", guardado.getId());
        return toResponse(guardado);
    }

    // Metodo solo para devolver solo algunos atributos y no todos como la contraseña
    private MiembroResponseDTO toResponse(Miembro miembro){
        return MiembroResponseDTO.builder()
                .id(miembro.getId())
                .nombres(miembro.getNombres())
                .apellidos(miembro.getApellidos())
                .documentoIdentidad(miembro.getDocumentoIdentidad())
                .correo(miembro.getCorreo())
                .telefono(miembro.getTelefono())
                .fechaNacimiento(miembro.getFechaNacimiento())
                .fechaRegistro(miembro.getFechaRegistro())
                .estado(miembro.getEstado())
                .build();
    }


    //Listar los miembros
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarMiembro() {
        return miembroRepository.findAll().stream().map(this::toResponse).toList();
    }

    // Obtener un miembro por id
    // La primera vez consulta la BD; las siguientes con el mismo id se leen de memoria
    @Cacheable(value = "miembros", key = "#id")
    @Transactional(readOnly = true)
    public MiembroResponseDTO obtenerPorId(Long id) {
        // Este mensaje solo aparece cuando el dato NO está en caché
        log.debug("Consultando miembro con id {} en la base de datos", id);
        return toResponse(buscar(id));
    }


    private Miembro buscar(Long id) {
        return miembroRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Miembro no encontrado con id " + id));
    }


    // Al actualizar se borra del caché para no devolver datos desactualizados
    @CacheEvict(value = "miembros", key = "#id")
    @Transactional
    public MiembroResponseDTO actualizar(Long id, MiembroUpdateDTO dto) {

        Miembro miembro = buscar(id);

        if (usuarioRepository.existsByCorreoAndIdNot(
                dto.getCorreo(), id)) {

            log.warn("No se pudo actualizar el miembro con id {}: correo repetido", id);
            throw new ConflictException(
                    "El correo ya está registrado en otra cuenta");
        }

        miembro.setNombres(dto.getNombres());
        miembro.setApellidos(dto.getApellidos());
        miembro.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        miembro.setCorreo(dto.getCorreo());
        miembro.setTelefono(dto.getTelefono());
        miembro.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getEstado() != null){
            //Si el estado pasa a inactivo entonces autamaticamente se libera ese cupo
            miembro.setEstado(dto.getEstado());
        }


        log.info("Miembro con id {} actualizado", id);
        return toResponse(miembroRepository.save(miembro));
    }

    // Eliminar un miembro
    @CacheEvict(value = "miembros", key = "#id")
    @Transactional
    public void eliminarMiembro(Long id) {
        if (!miembroRepository.existsById(id)) {
            log.warn("Se intentó eliminar un miembro inexistente con id {}", id);
            throw new NoSuchElementException("El miembro no existe.");
        }
        miembroRepository.deleteById(id);
        log.info("Miembro con id {} eliminado", id);
    }


}
