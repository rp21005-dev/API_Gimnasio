package gimnasio.service;
import gimnasio.dto.*;
import gimnasio.model.Miembro;
import gimnasio.repositoy.MiembroRepositoy;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class MiembroService {

    private MiembroRepositoy miembroRepositoy;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public MiembroResponseDTO crearMiembro(MiembroRequestDTO dto){
        if (miembroRepositoy.findByCorreo(dto.getCorreo()).isPresent()){
            throw new IllegalStateException("El correo ya existe ya existe.");
        }
        Miembro miembro = new Miembro();
        miembro.setNombres(dto.getNombres());
        miembro.setApellidos(dto.getApellidos());
        miembro.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        miembro.setCorreo(dto.getCorreo());
        miembro.setTelefono(dto.getTelefono());
        miembro.setFechaNacimiento(dto.getFechaNacimiento());
        miembro.setContrasenaHash(passwordEncoder.encode(dto.getContrasenaHash()));
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
                .build();
    }


    //Listar los entrenadores
    @Transactional(readOnly = true)
    public List<Miembro> listarMiembro() {
        return miembroRepositoy.findAll();
    }


    private Miembro buscar(Long id) {
        return miembroRepositoy.findById(id).orElseThrow(() -> new IllegalStateException("Miembro no encontrado con id " + id));
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
            throw new IllegalStateException("El miembro no existe.");
        }
        miembroRepositoy.deleteById(id);
    }


}
