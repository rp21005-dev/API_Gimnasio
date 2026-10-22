package gimnasio.service;
import gimnasio.dto.*;
import gimnasio.model.Entrenador;
import gimnasio.repositoy.EntrenadorRepository;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class EntrenadorService {

    private final EntrenadorRepository entrenadorRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public EntrenadorResponseDTO crearEntrenador(EntrenadorRequestDTO dto){
        if (entrenadorRepository.findByCorreo(dto.getCorreo()).isPresent()){
            throw new ConflictException("El correo ya existe.");
        }
        Entrenador entrenador = new Entrenador();
        entrenador.setNombres(dto.getNombres());
        entrenador.setApellidos(dto.getApellidos());
        entrenador.setCorreo(dto.getCorreo());
        entrenador.setTelefono(dto.getTelefono());
        entrenador.setEspecialidad(dto.getEspecialidad());
        entrenador.setContrasenaHash(passwordEncoder.encode(dto.getContrasenaHash()));
        entrenador.setEstado("ACTIVO");
        return toResponse(entrenadorRepository.save(entrenador));

    }
    // Metodo solo para devolver solo algunos atributos y no todos como la contraseña
    private EntrenadorResponseDTO toResponse(Entrenador entrenador){
        return EntrenadorResponseDTO.builder()
                .id(entrenador.getId())
                .nombres(entrenador.getNombres())
                .apellidos(entrenador.getApellidos())
                .nombreCompleto(entrenador.getNombreCompleto())
                .correo(entrenador.getCorreo())
                .telefono(entrenador.getTelefono())
                .especialidad(entrenador.getEspecialidad())
                .estado(entrenador.getEstado())
                .build();
    }

    //Listar los entrenadores
    @Transactional(readOnly = true)
    public List<EntrenadorResponseDTO> listarEntrenador() {
        return entrenadorRepository.findAll().stream().map(this::toResponse).toList();
    }

    // Obtener un entrenador por id
    @Transactional(readOnly = true)
    public EntrenadorResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }


    private Entrenador buscar(Long id) {
        return entrenadorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Entrenador no encontrado con id " + id));
    }


    @Transactional
    public EntrenadorResponseDTO actualizar(Long id, EntrenadorUpdateDTO dto) {

        Entrenador entrenador = buscar(id);

        if (entrenadorRepository.existsByCorreoAndIdNot(
                dto.getCorreo(), id)) {

            throw new ConflictException(
                    "El correo ya está registrado en otra cuenta");
        }

        entrenador.setNombres(dto.getNombres());
        entrenador.setApellidos(dto.getApellidos());
        entrenador.setCorreo(dto.getCorreo());
        entrenador.setTelefono(dto.getTelefono());
        entrenador.setEspecialidad(dto.getEspecialidad());
        entrenador.setEstado(dto.getEstado());

        return toResponse(entrenadorRepository.save(entrenador));
    }

    // Eliminar un entrenador por ID
    @Transactional
    public void eliminarEntrenador(Long id) {
        if (!entrenadorRepository.existsById(id)) {
            throw new NoSuchElementException("El entrenador no existe.");
        }
        entrenadorRepository.deleteById(id);
    }




}
