package gimnasio.service;

import gimnasio.dto.CategoriaRequestDTO;
import gimnasio.dto.CategoriaResponseDTO;
import gimnasio.dto.CategoriaUpdateDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Categoria;
import gimnasio.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j // Lombok crea el logger: private static final Logger log
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        String nombre = dto.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Intento de registrar una categoría con un nombre ya existente");
            throw new ConflictException("Ya existe una categoría con ese nombre.");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setObjetivoPrincipal(dto.getObjetivoPrincipal().trim());
        categoria.setTiposClase(limpiarTipos(dto.getTiposClase()));
        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoría creada con id {}", guardada.getId());
        return toResponse(guardada);
    }

    // Devuelve solo los datos necesarios en el formato de respuesta
    private CategoriaResponseDTO toResponse(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .objetivoPrincipal(categoria.getObjetivoPrincipal())
                .tiposClase(List.copyOf(categoria.getTiposClase()))
                .build();
    }

    // Quita espacios y valida que no haya tipos repetidos (sin importar mayúsculas)
    private List<String> limpiarTipos(List<String> tipos) {
        List<String> limpios = new ArrayList<>();
        Set<String> vistos = new HashSet<>();
        for (String tipo : tipos) {
            String t = tipo.trim();
            if (!vistos.add(t.toLowerCase())) {
                throw new IllegalArgumentException("Hay tipos de clase repetidos: " + t);
            }
            limpios.add(t);
        }
        return limpios;
    }

    // Listar las categorías
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    // Obtener una categoría por id
    // La primera vez consulta la BD; las siguientes con el mismo id se leen de memoria
    @Cacheable(value = "categorias", key = "#id")
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long id) {
        // Este mensaje solo aparece cuando el dato NO está en caché
        log.debug("Consultando categoría con id {} en la base de datos", id);
        return toResponse(buscar(id));
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con id " + id));
    }

    // Al actualizar se borra del caché para no devolver datos desactualizados
    @CacheEvict(value = "categorias", key = "#id")
    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaUpdateDTO dto) {
        Categoria categoria = buscar(id);
        String nombre = dto.getNombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            log.warn("No se pudo actualizar la categoría con id {}: nombre repetido", id);
            throw new ConflictException("Ya existe otra categoría con ese nombre.");
        }
        // TODO (cuando Clase tenga categoría): si se quita un tipo que usan clases activas -> ConflictException

        categoria.setNombre(nombre);
        categoria.setObjetivoPrincipal(dto.getObjetivoPrincipal().trim());
        categoria.getTiposClase().clear();
        categoria.getTiposClase().addAll(limpiarTipos(dto.getTiposClase()));

        log.info("Categoría con id {} actualizada", id);
        return toResponse(categoriaRepository.save(categoria));
    }

    // Eliminar una categoría
    @CacheEvict(value = "categorias", key = "#id")
    @Transactional
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            log.warn("Se intentó eliminar una categoría inexistente con id {}", id);
            throw new NoSuchElementException("La categoría no existe.");
        }
        // TODO (cuando Clase tenga categoría): si tiene clases activas -> ConflictException
        categoriaRepository.deleteById(id);
        log.info("Categoría con id {} eliminada", id);
    }
}
