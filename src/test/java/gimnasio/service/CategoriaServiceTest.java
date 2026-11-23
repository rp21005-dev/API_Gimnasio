package gimnasio.service;

import gimnasio.dto.CategoriaRequestDTO;
import gimnasio.dto.CategoriaResponseDTO;
import gimnasio.dto.CategoriaUpdateDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Categoria;
import gimnasio.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prueba unitaria: el repositorio es simulado con Mockito
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria(Long id, String nombre, String... tipos) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre(nombre);
        c.setObjetivoPrincipal("Objetivo");
        c.setTiposClase(new ArrayList<>(List.of(tipos)));
        return c;
    }

    @Test
    @DisplayName("Debe crear la categoría quitando los espacios sobrantes")
    void crearCategoria_Exito() {
        when(categoriaRepository.existsByNombreIgnoreCase("Cardio")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CategoriaResponseDTO resultado = categoriaService.crearCategoria(
                new CategoriaRequestDTO("  Cardio ", " Resistencia ", List.of(" Zumba", "Step ")));

        assertEquals(1L, resultado.getId());
        assertEquals("Cardio", resultado.getNombre());
        assertEquals("Resistencia", resultado.getObjetivoPrincipal());
        assertEquals(List.of("Zumba", "Step"), resultado.getTiposClase());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el nombre ya existe")
    void crearCategoria_NombreRepetido() {
        when(categoriaRepository.existsByNombreIgnoreCase("Cardio")).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoriaService.crearCategoria(
                new CategoriaRequestDTO("Cardio", "Objetivo", List.of("Zumba"))));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar tipos de clase repetidos sin importar mayúsculas")
    void crearCategoria_TiposRepetidos() {
        when(categoriaRepository.existsByNombreIgnoreCase("Cardio")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.crearCategoria(
                new CategoriaRequestDTO("Cardio", "Objetivo", List.of("Zumba", "zumba "))));
    }

    @Test
    @DisplayName("Debe listar todas las categorías")
    void listarCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(
                categoria(1L, "Cardio", "Zumba"), categoria(2L, "Fuerza", "BodyPump")));

        assertEquals(2, categoriaService.listarCategorias().size());
    }

    @Test
    @DisplayName("Debe obtener una categoría por ID")
    void obtenerPorId_Exito() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria(1L, "Cardio", "Zumba")));

        assertEquals("Cardio", categoriaService.obtenerPorId(1L).getNombre());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException si la categoría no existe")
    void obtenerPorId_NoEncontrada() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> categoriaService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Debe actualizar nombre, objetivo y tipos de la categoría")
    void actualizar_Exito() {
        Categoria existente = categoria(1L, "Cardio", "Zumba");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.existsByNombreIgnoreCaseAndIdNot("Cardio Plus", 1L)).thenReturn(false);
        when(categoriaRepository.save(existente)).thenReturn(existente);

        CategoriaResponseDTO resultado = categoriaService.actualizar(1L,
                new CategoriaUpdateDTO("Cardio Plus", "Nuevo objetivo", List.of("Step", "Aeróbicos")));

        assertEquals("Cardio Plus", resultado.getNombre());
        assertEquals(List.of("Step", "Aeróbicos"), resultado.getTiposClase());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si otra categoría ya usa el nombre")
    void actualizar_NombreRepetido() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria(1L, "Cardio", "Zumba")));
        when(categoriaRepository.existsByNombreIgnoreCaseAndIdNot("Fuerza", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoriaService.actualizar(1L,
                new CategoriaUpdateDTO("Fuerza", "Objetivo", List.of("Zumba"))));
    }

    @Test
    @DisplayName("Debe eliminar una categoría existente")
    void eliminar_Exito() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        categoriaService.eliminarCategoria(1L);

        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException al eliminar una categoría inexistente")
    void eliminar_NoEncontrada() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> categoriaService.eliminarCategoria(99L));
        verify(categoriaRepository, never()).deleteById(any());
    }
}
