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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prueba unitaria de las reglas de negocio de Clase (días, horario, cupo, traslapes)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // El setUp prepara datos que no todas las pruebas usan
class ClaseServiceTest {

    @Mock
    private ClaseRepository claseRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private SalaRepository salaRepository;
    @Mock
    private EntrenadorRepository entrenadorRepository;

    @InjectMocks
    private ClaseService claseService;

    private Categoria categoria;
    private Sala sala;
    private Entrenador entrenador;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Mente y Cuerpo", "Flexibilidad", new ArrayList<>(List.of("Yoga", "Pilates")));

        sala = new Sala();
        sala.setId(2L);
        sala.setNombre("Sala Mente y Cuerpo");
        sala.setCapacidadMaxima(20);

        entrenador = new Entrenador("Ana", "López", "7000-0000", "Yoga");
        entrenador.setId(3L);
        entrenador.setEstado("ACTIVO");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(salaRepository.findById(2L)).thenReturn(Optional.of(sala));
        when(entrenadorRepository.findById(3L)).thenReturn(Optional.of(entrenador));
        when(claseRepository.findByEntrenadorIdAndEstado(3L, "ACTIVA")).thenReturn(List.of());
        when(claseRepository.findBySalaIdAndEstado(2L, "ACTIVA")).thenReturn(List.of());
        when(claseRepository.save(any(Clase.class))).thenAnswer(inv -> {
            Clase c = inv.getArgument(0);
            if (c.getId() == null) c.setId(100L);
            return c;
        });
    }

    private ClaseRequestDTO request(List<String> dias, String inicio, String fin, int cupo, String tipo) {
        return new ClaseRequestDTO(tipo, dias, LocalTime.parse(inicio), LocalTime.parse(fin),
                LocalDate.now().plusDays(1), cupo, "Clase de prueba", 1L, 2L, 3L);
    }

    private Clase claseExistente(Long id, List<String> dias, String inicio, String fin) {
        Clase c = new Clase();
        c.setId(id);
        c.setTipoClase("Pilates");
        c.setDiasSemana(new ArrayList<>(dias));
        c.setHoraInicio(LocalTime.parse(inicio));
        c.setHoraFin(LocalTime.parse(fin));
        c.setEstado("ACTIVA");
        return c;
    }

    @Test
    @DisplayName("Debe crear una clase ACTIVA y normalizar los días (miercoles -> Miércoles)")
    void crearClase_Exito() {
        ClaseResponseDTO resultado = claseService.crearClase(
                request(List.of("lunes", "miercoles"), "08:00", "09:00", 15, "yoga"));

        assertEquals(100L, resultado.getId());
        assertEquals("ACTIVA", resultado.getEstado());
        assertEquals(List.of("Lunes", "Miércoles"), resultado.getDiasSemana());
        assertEquals("Sala Mente y Cuerpo", resultado.getSalaNombre());
        assertEquals("Ana López", resultado.getEntrenadorNombre());
    }

    @Test
    @DisplayName("Debe rechazar un día que no existe")
    void crearClase_DiaInvalido() {
        assertThrows(IllegalArgumentException.class, () -> claseService.crearClase(
                request(List.of("Lunes", "Feriado"), "08:00", "09:00", 10, "Yoga")));
    }

    @Test
    @DisplayName("Debe rechazar días repetidos")
    void crearClase_DiaRepetido() {
        assertThrows(IllegalArgumentException.class, () -> claseService.crearClase(
                request(List.of("Lunes", "lunes"), "08:00", "09:00", 10, "Yoga")));
    }

    @Test
    @DisplayName("Debe rechazar una hora de fin anterior o igual a la de inicio")
    void crearClase_HorarioInvalido() {
        assertThrows(IllegalArgumentException.class, () -> claseService.crearClase(
                request(List.of("Lunes"), "09:00", "09:00", 10, "Yoga")));
    }

    @Test
    @DisplayName("Debe rechazar un cupo de cero")
    void crearClase_CupoCero() {
        assertThrows(IllegalArgumentException.class, () -> claseService.crearClase(
                request(List.of("Lunes"), "08:00", "09:00", 0, "Yoga")));
    }

    @Test
    @DisplayName("Debe rechazar un tipo de clase que no pertenece a la categoría")
    void crearClase_TipoNoPerteneceACategoria() {
        assertThrows(IllegalArgumentException.class, () -> claseService.crearClase(
                request(List.of("Lunes"), "08:00", "09:00", 10, "Zumba")));
    }

    @Test
    @DisplayName("Debe rechazar un cupo mayor a la capacidad de la sala")
    void crearClase_CupoSuperaSala() {
        assertThrows(IllegalArgumentException.class, () -> claseService.crearClase(
                request(List.of("Lunes"), "08:00", "09:00", 21, "Yoga")));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el entrenador no está activo")
    void crearClase_EntrenadorInactivo() {
        entrenador.setEstado("INACTIVO");

        assertThrows(ConflictException.class, () -> claseService.crearClase(
                request(List.of("Lunes"), "08:00", "09:00", 10, "Yoga")));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el entrenador ya tiene clase en ese horario")
    void crearClase_TraslapeEntrenador() {
        when(claseRepository.findByEntrenadorIdAndEstado(3L, "ACTIVA"))
                .thenReturn(List.of(claseExistente(50L, List.of("Lunes"), "08:30", "09:30")));

        ConflictException e = assertThrows(ConflictException.class, () -> claseService.crearClase(
                request(List.of("Lunes"), "08:00", "09:00", 10, "Yoga")));
        assertTrue(e.getMessage().startsWith("El entrenador"));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si la sala ya está ocupada en ese horario")
    void crearClase_TraslapeSala() {
        when(claseRepository.findBySalaIdAndEstado(2L, "ACTIVA"))
                .thenReturn(List.of(claseExistente(51L, List.of("Martes"), "07:00", "08:30")));

        ConflictException e = assertThrows(ConflictException.class, () -> claseService.crearClase(
                request(List.of("Martes"), "08:00", "09:00", 10, "Yoga")));
        assertTrue(e.getMessage().startsWith("La sala"));
    }

    @Test
    @DisplayName("No hay traslape si las clases son en días distintos o una termina cuando la otra empieza")
    void crearClase_SinTraslape() {
        when(claseRepository.findByEntrenadorIdAndEstado(3L, "ACTIVA")).thenReturn(List.of(
                claseExistente(52L, List.of("Martes"), "08:00", "09:00"),
                claseExistente(53L, List.of("Lunes"), "07:00", "08:00")));

        assertNotNull(claseService.crearClase(request(List.of("Lunes"), "08:00", "09:00", 10, "Yoga")));
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException si la categoría, sala o entrenador no existen")
    void crearClase_RelacionInexistente() {
        ClaseRequestDTO dto = request(List.of("Lunes"), "08:00", "09:00", 10, "Yoga");
        dto.setSalaId(99L);
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> claseService.crearClase(dto));
    }

    @Test
    @DisplayName("Al actualizar, la clase no choca consigo misma")
    void actualizar_NoChocaConsigoMisma() {
        Clase existente = claseExistente(60L, List.of("Lunes"), "08:00", "09:00");
        when(claseRepository.findById(60L)).thenReturn(Optional.of(existente));
        when(claseRepository.findByEntrenadorIdAndEstado(3L, "ACTIVA")).thenReturn(List.of(existente));

        ClaseUpdateDTO dto = new ClaseUpdateDTO("Pilates", List.of("Lunes"), LocalTime.of(8, 0), LocalTime.of(9, 30),
                LocalDate.now(), 12, null, "ACTIVA", 1L, 2L, 3L);

        ClaseResponseDTO resultado = claseService.actualizar(60L, dto);

        assertEquals(LocalTime.of(9, 30), resultado.getHoraFin());
        assertEquals(12, resultado.getCupoMaximo());
    }

    @Test
    @DisplayName("Una clase CERRADA no revisa traslapes ni el estado del entrenador")
    void actualizar_ClaseCerrada() {
        Clase existente = claseExistente(61L, List.of("Lunes"), "08:00", "09:00");
        when(claseRepository.findById(61L)).thenReturn(Optional.of(existente));
        entrenador.setEstado("INACTIVO");

        ClaseUpdateDTO dto = new ClaseUpdateDTO("Pilates", List.of("Lunes"), LocalTime.of(8, 0), LocalTime.of(9, 0),
                LocalDate.now(), 10, null, "CERRADA", 1L, 2L, 3L);

        assertEquals("CERRADA", claseService.actualizar(61L, dto).getEstado());
        verify(claseRepository, never()).findByEntrenadorIdAndEstado(any(), any());
    }

    @Test
    @DisplayName("Debe listar, obtener y eliminar clases")
    void listarObtenerEliminar() {
        Clase existente = claseExistente(70L, List.of("Viernes"), "18:00", "19:00");
        when(claseRepository.findAll()).thenReturn(List.of(existente));
        when(claseRepository.findById(70L)).thenReturn(Optional.of(existente));
        when(claseRepository.existsById(70L)).thenReturn(true);

        assertEquals(1, claseService.listarClases().size());
        // Una clase sin relaciones (datos antiguos) no debe fallar al convertirse a DTO
        assertNull(claseService.obtenerPorId(70L).getSalaId());

        claseService.eliminarClase(70L);
        verify(claseRepository).deleteById(70L);
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException al obtener o eliminar una clase inexistente")
    void claseInexistente() {
        when(claseRepository.findById(99L)).thenReturn(Optional.empty());
        when(claseRepository.existsById(99L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> claseService.obtenerPorId(99L));
        assertThrows(NoSuchElementException.class, () -> claseService.eliminarClase(99L));
    }
}
