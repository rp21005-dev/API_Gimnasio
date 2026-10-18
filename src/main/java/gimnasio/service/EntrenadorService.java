package gimnasio.service;
import gimnasio.dto.AdministradorRequestDTO;
import gimnasio.dto.EntrenadorRequestDTO;
import gimnasio.dto.EntrenadorUpdateDTO;
import gimnasio.model.Administrador;
import gimnasio.model.Entrenador;
import gimnasio.repositoy.EntrenadorRepository;
import gimnasio.dto.EntrenadorResponseDTO;
import gimnasio.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class EntrenadorService {

    private EntrenadorRepository entrenadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Entrenador crearEntrenador(EntrenadorRequestDTO dto){
        if (entrenadorRepository.findByCorreo(dto.getCorreo()).isPresent()){
            throw new IllegalStateException("El correo ya existe ya existe.");
        }
        Entrenador entrenador = new Entrenador();
        entrenador.setNombres(dto.getNombres());
        entrenador.setApellidos(dto.getApellidos());
        entrenador.setCorreo(dto.getCorreo());
        entrenador.setTelefono(dto.getTelefono());
        entrenador.setEspecialidad(dto.getEspecialidad());
        entrenador.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        entrenador.setEstado("ACTIVO");
        return toResponse(entrenadorRepository.save(e));
        return administradorRepository.save(administrador);
    }

}
