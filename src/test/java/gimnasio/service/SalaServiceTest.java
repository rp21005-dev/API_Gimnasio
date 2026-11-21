package gimnasio.service;

import gimnasio.dto.SalaRequestDTO;
import gimnasio.dto.SalaResponseDTO;
import gimnasio.dto.SalaUpdateDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Sala;
import gimnasio.repository.SalaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prueba unitaria: el repositorio es simulado con Mockito
@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    private Sala sala(Long id, String codigo, String nombre, int capacidad) {
        Sala s = new Sala();
        s.setId(id);
        s.setCodigo(codigo);
        s.setNombre(nombre);
        s.setCapacidadMaxima(capacidad);
        return s;
    }

    @Test
    @DisplayName("Debe crear una sala con código y nombre sin espacios sobrantes")
    void crearSala_Exito() {
        when(salaRepository.existsByCodigoIgnoreCase("S-10")).thenReturn(false);
        when(salaRepository.existsByNombreIgnoreCase("Sala Azul")).thenReturn(false);
        when(salaRepository.save(any(Sala.class))).thenAnswer(inv -> {
            Sala s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        SalaResponseDTO resultado = salaService.crearSala(
                new SalaRequestDTO(" S-10 ", " Sala Azul ", "Primer piso", 15, "Funcional"));

        assertEquals(10L, resultado.getId());
        assertEquals("S-10", resultado.getCodigo());
        assertEquals("Sala Azul", resultado.getNombre());
        assertEquals(15, resultado.getCapacidadMaxima());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el código ya existe")
    void crearSala_CodigoRepetido() {
        when(salaRepository.existsByCodigoIgnoreCase("S-01")).thenReturn(true);

        assertThrows(ConflictException.class, () -> salaService.crearSala(
                new SalaRequestDTO("S-01", "Otra", null, 10, null)));
        verify(salaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el nombre ya existe")
    void crearSala_NombreRepetido() {
        when(salaRepository.existsByCodigoIgnoreCase("S-99")).thenReturn(false);
        when(salaRepository.existsByNombreIgnoreCase("Sala Multiuso")).thenReturn(true);

        assertThrows(ConflictException.class, () -> salaService.crearSala(
                new SalaRequestDTO("S-99", "Sala Multiuso", null, 10, null)));
    }

    @Test
    @DisplayName("Debe listar las salas y obtener una por ID")
    void listarYObtener() {
        Sala s = sala(1L, "S-01", "Ciclismo", 25);
        when(salaRepository.findAll()).thenReturn(List.of(s));
        when(salaRepository.findById(1L)).thenReturn(Optional.of(s));

        assertEquals(1, salaService.listarSalas().size());
        assertEquals("Ciclismo", salaService.obtenerPorId(1L).getNombre());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException si la sala no existe")
    void obtenerPorId_NoEncontrada() {
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> salaService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Debe actualizar los datos de la sala")
    void actualizar_Exito() {
        Sala existente = sala(1L, "S-01", "Ciclismo", 25);
        when(salaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(salaRepository.existsByCodigoIgnoreCaseAndIdNot("S-01", 1L)).thenReturn(false);
        when(salaRepository.existsByNombreIgnoreCaseAndIdNot("Ciclismo Pro", 1L)).thenReturn(false);
        when(salaRepository.save(existente)).thenReturn(existente);

        SalaResponseDTO resultado = salaService.actualizar(1L,
                new SalaUpdateDTO("S-01", "Ciclismo Pro", "Segundo piso", 30, "Renovada"));

        assertEquals("Ciclismo Pro", resultado.getNombre());
        assertEquals(30, resultado.getCapacidadMaxima());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si otra sala ya usa el código")
    void actualizar_CodigoRepetido() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala(1L, "S-01", "Ciclismo", 25)));
        when(salaRepository.existsByCodigoIgnoreCaseAndIdNot("S-02", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> salaService.actualizar(1L,
                new SalaUpdateDTO("S-02", "Ciclismo", null, 25, null)));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si otra sala ya usa el nombre")
    void actualizar_NombreRepetido() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala(1L, "S-01", "Ciclismo", 25)));
        when(salaRepository.existsByCodigoIgnoreCaseAndIdNot("S-01", 1L)).thenReturn(false);
        when(salaRepository.existsByNombreIgnoreCaseAndIdNot("Sala Multiuso", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> salaService.actualizar(1L,
                new SalaUpdateDTO("S-01", "Sala Multiuso", null, 25, null)));
    }

    @Test
    @DisplayName("Debe eliminar una sala existente y fallar si no existe")
    void eliminar() {
        when(salaRepository.existsById(1L)).thenReturn(true);
        when(salaRepository.existsById(99L)).thenReturn(false);

        salaService.eliminarSala(1L);

        verify(salaRepository).deleteById(1L);
        assertThrows(NoSuchElementException.class, () -> salaService.eliminarSala(99L));
    }
}
