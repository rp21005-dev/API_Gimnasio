package gimnasio.service;
import gimnasio.dto.ClaseRequestDTO;
import gimnasio.dto.ClaseResponseDTO;
import gimnasio.dto.ClaseUpdateDTO;
import gimnasio.model.Clase;
import gimnasio.repository.ClaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j // Lombok crea el logger: private static final Logger log
@Service
@RequiredArgsConstructor
public class ClaseService {

    private final ClaseRepository claseRepository;

    @Transactional
    public ClaseResponseDTO crearClase(ClaseRequestDTO dto) {
        Clase clase = new Clase();
        clase.setTipoClase(dto.getTipoClase());
        clase.setDiasSemana(dto.getDiasSemana());
        clase.setHoraInicio(dto.getHoraInicio());
        clase.setHoraFin(dto.getHoraFin());
        clase.setFechaInicio(dto.getFechaInicio());
        clase.setCupoMaximo(dto.getCupoMaximo());
        clase.setDescripcion(dto.getDescripcion());
        clase.setEstado("ACTIVA");
        Clase guardada = claseRepository.save(clase);
        log.info("Clase creada con id {}", guardada.getId());
        return toResponse(guardada);
    }

    private ClaseResponseDTO toResponse(Clase clase) {
        return ClaseResponseDTO.builder()
                .id(clase.getId())
                .tipoClase(clase.getTipoClase())
                .diasSemana(List.copyOf(clase.getDiasSemana()))
                .horaInicio(clase.getHoraInicio())
                .horaFin(clase.getHoraFin())
                .fechaInicio(clase.getFechaInicio())
                .cupoMaximo(clase.getCupoMaximo())
                .descripcion(clase.getDescripcion())
                .estado(clase.getEstado())
                .build();
    }

    //Listar las clases
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> listarClases() {
        return claseRepository.findAll().stream().map(this::toResponse).toList();
    }

    // Obtener una clase por id
    // La primera vez consulta la BD; las siguientes con el mismo id se leen de memoria
    @Cacheable(value = "clases", key = "#id")
    @Transactional(readOnly = true)
    public ClaseResponseDTO obtenerPorId(Long id) {
        // Este mensaje solo aparece cuando el dato NO está en caché
        log.debug("Consultando clase con id {} en la base de datos", id);
        return toResponse(buscar(id));
    }

    private Clase buscar(Long id) {
        return claseRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Clase no encontrada con id " + id));
    }

    // Al actualizar se borra del caché para no devolver datos desactualizados
    @CacheEvict(value = "clases", key = "#id")
    @Transactional
    public ClaseResponseDTO actualizar(Long id, ClaseUpdateDTO dto) {
        Clase clase = buscar(id);
        clase.setTipoClase(dto.getTipoClase());
        clase.setDiasSemana(dto.getDiasSemana());
        clase.setHoraInicio(dto.getHoraInicio());
        clase.setHoraFin(dto.getHoraFin());
        clase.setFechaInicio(dto.getFechaInicio());
        clase.setCupoMaximo(dto.getCupoMaximo());
        clase.setDescripcion(dto.getDescripcion());
        clase.setEstado(dto.getEstado());

        log.info("Clase con id {} actualizada", id);
        return toResponse(claseRepository.save(clase));
    }

    // Eliminar una clase por ID
    @CacheEvict(value = "clases", key = "#id")
    @Transactional
    public void eliminarClase(Long id) {
        if (!claseRepository.existsById(id)) {
            log.warn("Se intentó eliminar una clase inexistente con id {}", id);
            throw new NoSuchElementException("La clase no existe.");
        }
        claseRepository.deleteById(id);
        log.info("Clase con id {} eliminada", id);
    }
}
