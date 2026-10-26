package gimnasio.service;

import gimnasio.dto.ClaseRequestDTO;
import gimnasio.dto.ClaseResponseDTO;
import gimnasio.dto.ClaseUpdateDTO;
import gimnasio.model.Clase;
import gimnasio.repository.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClaseService {

    @Autowired
    private ClaseRepository claseRepository;

    public List<ClaseResponseDTO> obtenerTodas() {
        return claseRepository.findAll()
                .stream()
                .map(this::convertirAEntityAResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ClaseResponseDTO> obtenerPorId(int id) {
        return claseRepository.findById(id)
                .map(this::convertirAEntityAResponseDTO);
    }

    public ClaseResponseDTO guardar(ClaseRequestDTO dto) {
        Clase clase = new Clase();
        clase.setTipoClase(dto.getTipoClase());
        clase.setDiasSemana(dto.getDiasSemana());
        clase.setHoraInicio(dto.getHoraInicio());
        clase.setHoraFin(dto.getHoraFin());
        clase.setFechaInicio(dto.getFechaInicio());
        clase.setCupoMaximo(dto.getCupoMaximo());
        clase.setDescripcion(dto.getDescripcion());
        clase.setEstado(dto.getEstado());

        Clase guardada = claseRepository.save(clase);
        return convertirAEntityAResponseDTO(guardada);
    }

    public Optional<ClaseResponseDTO> actualizar(int id, ClaseUpdateDTO dto) {
        return claseRepository.findById(id).map(clase -> {
            if (dto.getTipoClase() != null) clase.setTipoClase(dto.getTipoClase());
            if (dto.getDiasSemana() != null) clase.setDiasSemana(dto.getDiasSemana());
            if (dto.getHoraInicio() != null) clase.setHoraInicio(dto.getHoraInicio());
            if (dto.getHoraFin() != null) clase.setHoraFin(dto.getHoraFin());
            if (dto.getFechaInicio() != null) clase.setFechaInicio(dto.getFechaInicio());
            if (dto.getCupoMaximo() != null) clase.setCupoMaximo(dto.getCupoMaximo());
            if (dto.getDescripcion() != null) clase.setDescripcion(dto.getDescripcion());
            if (dto.getEstado() != null) clase.setEstado(dto.getEstado());

            Clase actualizada = claseRepository.save(clase);
            return convertirAEntityAResponseDTO(actualizada);
        });
    }

    public boolean eliminar(int id) {
        if (claseRepository.existsById(id)) {
            claseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ClaseResponseDTO convertirAEntityAResponseDTO(Clase clase) {
        return new ClaseResponseDTO(
                clase.getId(),
                clase.getTipoClase(),
                clase.getDiasSemana(),
                clase.getHoraInicio(),
                clase.getHoraFin(),
                clase.getFechaInicio(),
                clase.getCupoMaximo(),
                clase.getDescripcion(),
                clase.getEstado()
        );
    }
}