package gimnasio.service;

import gimnasio.dto.SalaRequestDTO;
import gimnasio.dto.SalaResponseDTO;
import gimnasio.dto.SalaUpdateDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Sala;
import gimnasio.repository.SalaRepository;
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
public class SalaService {

    private final SalaRepository salaRepository;

    @Transactional
    public SalaResponseDTO crearSala(SalaRequestDTO dto) {
        String codigo = dto.getCodigo().trim();
        String nombre = dto.getNombre().trim();

        if (salaRepository.existsByCodigoIgnoreCase(codigo)) {
            log.warn("Intento de registrar una sala con un código ya existente");
            throw new ConflictException("Ya existe una sala con ese código.");
        }
        if (salaRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Intento de registrar una sala con un nombre ya existente");
            throw new ConflictException("Ya existe una sala con ese nombre.");
        }

        Sala sala = new Sala();
        sala.setCodigo(codigo);
        sala.setNombre(nombre);
        sala.setUbicacion(dto.getUbicacion());
        sala.setCapacidadMaxima(dto.getCapacidadMaxima());
        sala.setDescripcion(dto.getDescripcion());
        Sala guardada = salaRepository.save(sala);
        log.info("Sala creada con id {}", guardada.getId());
        return toResponse(guardada);
    }

    private SalaResponseDTO toResponse(Sala sala) {
        return SalaResponseDTO.builder()
                .id(sala.getId())
                .codigo(sala.getCodigo())
                .nombre(sala.getNombre())
                .ubicacion(sala.getUbicacion())
                .capacidadMaxima(sala.getCapacidadMaxima())
                .descripcion(sala.getDescripcion())
                .build();
    }

    // Listar las salas
    @Transactional(readOnly = true)
    public List<SalaResponseDTO> listarSalas() {
        return salaRepository.findAll().stream().map(this::toResponse).toList();
    }

    // Obtener una sala por id
    // La primera vez consulta la BD; las siguientes con el mismo id se leen de memoria
    @Cacheable(value = "salas", key = "#id")
    @Transactional(readOnly = true)
    public SalaResponseDTO obtenerPorId(Long id) {
        // Este mensaje solo aparece cuando el dato NO está en caché
        log.debug("Consultando sala con id {} en la base de datos", id);
        return toResponse(buscar(id));
    }

    private Sala buscar(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sala no encontrada con id " + id));
    }

    // Al actualizar se borra del caché para no devolver datos desactualizados
    @CacheEvict(value = "salas", key = "#id")
    @Transactional
    public SalaResponseDTO actualizar(Long id, SalaUpdateDTO dto) {
        Sala sala = buscar(id);
        String codigo = dto.getCodigo().trim();
        String nombre = dto.getNombre().trim();

        if (salaRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            log.warn("No se pudo actualizar la sala con id {}: código repetido", id);
            throw new ConflictException("Ya existe otra sala con ese código.");
        }
        if (salaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            log.warn("No se pudo actualizar la sala con id {}: nombre repetido", id);
            throw new ConflictException("Ya existe otra sala con ese nombre.");
        }
        // TODO (cuando Clase tenga sala): si la nueva capacidad es menor al cupo de una clase activa -> ConflictException

        sala.setCodigo(codigo);
        sala.setNombre(nombre);
        sala.setUbicacion(dto.getUbicacion());
        sala.setCapacidadMaxima(dto.getCapacidadMaxima());
        sala.setDescripcion(dto.getDescripcion());

        log.info("Sala con id {} actualizada", id);
        return toResponse(salaRepository.save(sala));
    }

    // Eliminar una sala
    @CacheEvict(value = "salas", key = "#id")
    @Transactional
    public void eliminarSala(Long id) {
        if (!salaRepository.existsById(id)) {
            log.warn("Se intentó eliminar una sala inexistente con id {}", id);
            throw new NoSuchElementException("La sala no existe.");
        }
        // TODO (cuando Clase tenga sala): si tiene clases activas -> ConflictException
        salaRepository.deleteById(id);
        log.info("Sala con id {} eliminada", id);
    }
}
