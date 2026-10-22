package gimnasio.service;
import gimnasio.dto.*;
import gimnasio.model.Miembro;
import gimnasio.repositoy.MiembroRepositoy;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MiembroService {

    private final MiembroRepositoy miembroRepositoy;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public MiembroResponseDTO crearMiembro(MiembroRequestDTO dto){
        if (miembroRepositoy.findByCorreo(dto.getCorreo()).isPresent()){
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
        return toResponse(miembroRepositoy.save(miembro));
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
        return miembroRepositoy.findAll().stream().map(this::toResponse).toList();
    }

    // Obtener un miembro por id
    @Transactional(readOnly = true)
    public MiembroResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }


    private Miembro buscar(Long id) {
        return miembroRepositoy.findById(id).orElseThrow(() -> new NoSuchElementException("Miembro no encontrado con id " + id));
    }


    @Transactional
    public MiembroResponseDTO actualizar(Long id, MiembroUpdateDTO dto) {

        Miembro miembro = buscar(id);

        if (miembroRepositoy.existsByCorreoAndIdNot(
                dto.getCorreo(), id)) {

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


        return toResponse(miembroRepositoy.save(miembro));
    }

    // Eliminar un miembro
    @Transactional
    public void eliminarMiembro(Long id) {
        if (!miembroRepositoy.existsById(id)) {
            throw new NoSuchElementException("El miembro no existe.");
        }
        miembroRepositoy.deleteById(id);
    }


}
