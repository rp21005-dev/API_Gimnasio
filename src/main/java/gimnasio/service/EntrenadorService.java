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


@Service
@RequiredArgsConstructor
public class EntrenadorService {

    private EntrenadorRepository entrenadorRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public EntrenadorResponseDTO crearEntrenador(EntrenadorRequestDTO dto){
        if (entrenadorRepository.findByCorreo(dto.getCorreo()).isPresent()){
            throw new IllegalStateException("El correo ya existe ya existe.");
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
    public List<Entrenador> listarEntrenador() {
        return entrenadorRepository.findAll();
    }


    private Entrenador buscar(Long id) {
        return entrenadorRepository.findById(id).orElseThrow(() -> new IllegalStateException("Entrenador no encontrado con id " + id));
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

        Entrenador actualizado = entrenadorRepository.save(entrenador);
        return EntrenadorResponseDTO.builder()
                .correo(actualizado.getCorreo())
                .estado(actualizado.getEstado())
                .build();
    }

    // Eliminar un entrenador por ID
    @Transactional
    public void eliminarEntrenador(Long id) {
        if (!entrenadorRepository.existsById(id)) {
            throw new IllegalStateException("El administrador no existe.");
        }
        entrenadorRepository.deleteById(id);
    }




}
