package gimnasio.service;

import gimnasio.dto.ClaseRequestDTO;
import gimnasio.dto.ClaseResponseDTO;
import gimnasio.dto.ClaseUpdateDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Categoria;
import gimnasio.model.Clase;
import gimnasio.model.Entrenador;
import gimnasio.model.Sala;
import gimnasio.repository.CategoriaRepository;
import gimnasio.repository.ClaseRepository;
import gimnasio.repository.EntrenadorRepository;
import gimnasio.repository.SalaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j // Lombok crea el logger: private static final Logger log
@Service
@RequiredArgsConstructor
public class ClaseService {

    private static final String ACTIVA = "ACTIVA";
    private static final List<String> DIAS_VALIDOS =
            List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

    private final ClaseRepository claseRepository;
    private final CategoriaRepository categoriaRepository;
    private final SalaRepository salaRepository;
    private final EntrenadorRepository entrenadorRepository;

    @Transactional
    public ClaseResponseDTO crearClase(ClaseRequestDTO dto) {
        Categoria categoria = buscarCategoria(dto.getCategoriaId());
        Sala sala = buscarSala(dto.getSalaId());
        Entrenador entrenador = buscarEntrenador(dto.getEntrenadorId());
        List<String> dias = normalizarDias(dto.getDiasSemana());

        // Una clase nueva siempre nace ACTIVA
        validarClase(dto.getTipoClase(), dto.getCupoMaximo(), dto.getHoraInicio(), dto.getHoraFin(),
                dias, categoria, sala, entrenador, ACTIVA, null);

        Clase clase = new Clase();
        clase.setTipoClase(dto.getTipoClase().trim());
        clase.setDiasSemana(new ArrayList<>(dias));
        clase.setHoraInicio(dto.getHoraInicio());
        clase.setHoraFin(dto.getHoraFin());
        clase.setFechaInicio(dto.getFechaInicio());
        clase.setCupoMaximo(dto.getCupoMaximo());
        clase.setDescripcion(dto.getDescripcion());
        clase.setEstado(ACTIVA);
        clase.setCategoria(categoria);
        clase.setSala(sala);
        clase.setEntrenador(entrenador);

        Clase guardada = claseRepository.save(clase);
        log.info("Clase creada con id {}", guardada.getId());
        return toResponse(guardada);
    }

    // Devuelve solo los datos necesarios; las relaciones pueden ser null en clases antiguas
    private ClaseResponseDTO toResponse(Clase clase) {
        Categoria cat = clase.getCategoria();
        Sala sala = clase.getSala();
        Entrenador ent = clase.getEntrenador();

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
                .categoriaId(cat != null ? cat.getId() : null)
                .categoriaNombre(cat != null ? cat.getNombre() : null)
                .salaId(sala != null ? sala.getId() : null)
                .salaNombre(sala != null ? sala.getNombre() : null)
                .entrenadorId(ent != null ? ent.getId() : null)
                .entrenadorNombre(ent != null ? ent.getNombreCompleto() : null)
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
        return claseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Clase no encontrada con id " + id));
    }

    // Al actualizar se borra del caché para no devolver datos desactualizados
    @CacheEvict(value = "clases", key = "#id")
    @Transactional
    public ClaseResponseDTO actualizar(Long id, ClaseUpdateDTO dto) {
        Clase clase = buscar(id);

        Categoria categoria = buscarCategoria(dto.getCategoriaId());
        Sala sala = buscarSala(dto.getSalaId());
        Entrenador entrenador = buscarEntrenador(dto.getEntrenadorId());
        List<String> dias = normalizarDias(dto.getDiasSemana());

        // TODO (cuando se pueda contar inscritos): el nuevo cupo no debe ser menor a los inscritos actuales

        // Se pasa el id para que la clase no choque consigo misma al revisar traslapes
        validarClase(dto.getTipoClase(), dto.getCupoMaximo(), dto.getHoraInicio(), dto.getHoraFin(),
                dias, categoria, sala, entrenador, dto.getEstado(), id);

        clase.setTipoClase(dto.getTipoClase().trim());
        clase.setDiasSemana(new ArrayList<>(dias));
        clase.setHoraInicio(dto.getHoraInicio());
        clase.setHoraFin(dto.getHoraFin());
        clase.setFechaInicio(dto.getFechaInicio());
        clase.setCupoMaximo(dto.getCupoMaximo());
        clase.setDescripcion(dto.getDescripcion());
        clase.setEstado(dto.getEstado());
        clase.setCategoria(categoria);
        clase.setSala(sala);
        clase.setEntrenador(entrenador);

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

    // ---------------------------------------------------------------
    // Búsquedas de las relaciones (404 si no existen)
    // ---------------------------------------------------------------
    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con id " + id));
    }

    private Sala buscarSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sala no encontrada con id " + id));
    }

    private Entrenador buscarEntrenador(Long id) {
        return entrenadorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Entrenador no encontrado con id " + id));
    }

    // ---------------------------------------------------------------
    // Reglas de negocio de la clase
    // ---------------------------------------------------------------

    // Acepta "lunes", "Lunes" o "miercoles" y los guarda siempre como "Lunes", "Miércoles"...
    private List<String> normalizarDias(List<String> dias) {
        List<String> resultado = new ArrayList<>();
        for (String dia : dias) {
            String buscado = sinAcentos(dia.trim());
            String canonico = DIAS_VALIDOS.stream()
                    .filter(v -> sinAcentos(v).equalsIgnoreCase(buscado))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Día inválido: '" + dia + "'. Use: " + DIAS_VALIDOS));
            if (resultado.contains(canonico)) {
                throw new IllegalArgumentException("Día repetido: " + canonico);
            }
            resultado.add(canonico);
        }
        return resultado;
    }

    private String sinAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private void validarClase(String tipo, int cupo, LocalTime inicio, LocalTime fin, List<String> dias,
                              Categoria categoria, Sala sala, Entrenador entrenador,
                              String estadoResultante, Long claseIdExcluir) {

        // 400: datos inválidos
        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser mayor a la hora de inicio.");
        }
        if (cupo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor a cero.");
        }

        // El tipo de clase debe ser uno de los tipos de la categoría elegida
        boolean tipoValido = categoria.getTiposClase().stream().anyMatch(t -> t.equalsIgnoreCase(tipo.trim()));
        if (!tipoValido) {
            throw new IllegalArgumentException("El tipo de clase no pertenece a la categoría seleccionada. Tipos válidos: "
                    + categoria.getTiposClase());
        }

        // El cupo no puede superar la capacidad de la sala
        if (cupo > sala.getCapacidadMaxima()) {
            throw new IllegalArgumentException("El cupo máximo (" + cupo
                    + ") supera la capacidad de la sala (" + sala.getCapacidadMaxima() + ").");
        }

        // Una clase que queda cerrada no ocupa entrenador ni sala: no se revisan traslapes
        if (!ACTIVA.equalsIgnoreCase(estadoResultante)) {
            return;
        }

        // 409: conflictos con otras clases o con el estado del entrenador
        if (!"ACTIVO".equalsIgnoreCase(entrenador.getEstado())) {
            throw new ConflictException("El entrenador no está activo.");
        }
        verificarTraslape(claseRepository.findByEntrenadorIdAndEstado(entrenador.getId(), ACTIVA),
                dias, inicio, fin, claseIdExcluir, "El entrenador");
        verificarTraslape(claseRepository.findBySalaIdAndEstado(sala.getId(), ACTIVA),
                dias, inicio, fin, claseIdExcluir, "La sala");
    }

    // Lanza 409 si alguna clase existente comparte un día y su horario se cruza con el nuevo
    private void verificarTraslape(List<Clase> existentes, List<String> dias, LocalTime inicio, LocalTime fin,
                                   Long claseIdExcluir, String quien) {
        for (Clase c : existentes) {
            if (claseIdExcluir != null && c.getId().equals(claseIdExcluir)) {
                continue;
            }
            boolean diaEnComun = c.getDiasSemana().stream().anyMatch(dias::contains);
            boolean horarioCruzado = inicio.isBefore(c.getHoraFin()) && c.getHoraInicio().isBefore(fin);

            if (diaEnComun && horarioCruzado) {
                log.warn("Traslape detectado con la clase {}", c.getId());
                throw new ConflictException(quien + " ya tiene la clase '" + c.getTipoClase()
                        + "' en ese día y horario (" + c.getHoraInicio() + " - " + c.getHoraFin() + ").");
            }
        }
    }
}