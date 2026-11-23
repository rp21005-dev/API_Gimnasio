package gimnasio.service;

import gimnasio.dto.InscripcionRequestDTO;
import gimnasio.dto.InscripcionResponseDTO;
import gimnasio.dto.InscripcionUpdateDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Clase;
import gimnasio.model.Inscripcion;
import gimnasio.model.Miembro;
import gimnasio.repository.ClaseRepository;
import gimnasio.repository.MiembroRepository;
import gimnasio.repository.InscripcionRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prueba unitaria de las reglas de inscripción (CU-06): estado, duplicidad, traslape y cupo
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // El setUp prepara datos que no todas las pruebas usan
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private MiembroRepository miembroRepository;
    @Mock
    private ClaseRepository claseRepository;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Miembro miembro;
    private Clase yoga;

    @BeforeEach
    void setUp() {
        miembro = new Miembro();
        miembro.setId(5L);
        miembro.setCorreo("miembro@ejemplo.com");
        miembro.setEstado("ACTIVO");

        yoga = clase(10L, "Yoga", List.of("Lunes", "Miércoles"), "08:00", "09:00", 2);

        when(miembroRepository.findById(5L)).thenReturn(Optional.of(miembro));
        when(claseRepository.findById(10L)).thenReturn(Optional.of(yoga));
        when(inscripcionRepository.findByMiembroId(5L)).thenReturn(List.of());
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(inv -> {
            Inscripcion i = inv.getArgument(0);
            if (i.getId() == null) i.setId(1L);
            return i;
        });
    }

    private Clase clase(Long id, String tipo, List<String> dias, String inicio, String fin, int cupo) {
        Clase c = new Clase();
        c.setId(id);
        c.setTipoClase(tipo);
        c.setDiasSemana(new ArrayList<>(dias));
        c.setHoraInicio(LocalTime.parse(inicio));
        c.setHoraFin(LocalTime.parse(fin));
        c.setCupoMaximo(cupo);
        c.setEstado("ACTIVA");
        return c;
    }

    private Inscripcion inscripcion(Long id, Clase clase, String estado) {
        Inscripcion i = new Inscripcion(miembro, clase, LocalDate.now(), estado);
        i.setId(id);
        return i;
    }

    @Test
    @DisplayName("Debe inscribir al miembro y calcular el cupo restante")
    void inscribir_Exito() {
        when(inscripcionRepository.countByClaseIdAndEstado(10L, "activa")).thenReturn(0L);

        InscripcionResponseDTO resultado = inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, 5L), null);

        assertEquals(1L, resultado.getId());
        assertEquals(5L, resultado.getMiembroId());
        assertEquals(10L, resultado.getClaseId());
        assertEquals("activa", resultado.getEstado());
        assertEquals(1, resultado.getCupoDisponibleRestante());
    }

    @Test
    @DisplayName("Si no se envía miembroId se usa el usuario autenticado")
    void inscribir_UsaUsuarioAutenticado() {
        InscripcionResponseDTO resultado = inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, null), 5L);

        assertEquals(5L, resultado.getMiembroId());
    }

    @Test
    @DisplayName("Debe rechazar la inscripción sin miembro")
    void inscribir_SinMiembro() {
        assertThrows(IllegalArgumentException.class,
                () -> inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, null), null));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el miembro no está activo")
    void inscribir_MiembroInactivo() {
        miembro.setEstado("INACTIVO");

        assertThrows(ConflictException.class,
                () -> inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, 5L), null));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si la clase está cerrada")
    void inscribir_ClaseCerrada() {
        yoga.setEstado("CERRADA");

        assertThrows(ConflictException.class,
                () -> inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, 5L), null));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si ya está inscrito en la clase")
    void inscribir_Duplicado() {
        when(inscripcionRepository.existsByMiembroIdAndClaseIdAndEstado(5L, 10L, "activa")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, 5L), null));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si se traslapa con otra clase del miembro")
    void inscribir_Traslape() {
        Clase pilates = clase(11L, "Pilates", List.of("Miércoles"), "08:30", "09:30", 10);
        when(inscripcionRepository.findByMiembroId(5L)).thenReturn(List.of(inscripcion(2L, pilates, "activa")));

        ConflictException e = assertThrows(ConflictException.class,
                () -> inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, 5L), null));
        assertTrue(e.getMessage().contains("Pilates"));
    }

    @Test
    @DisplayName("Las inscripciones canceladas no cuentan para el traslape")
    void inscribir_IgnoraCanceladas() {
        Clase pilates = clase(11L, "Pilates", List.of("Lunes"), "08:00", "09:00", 10);
        when(inscripcionRepository.findByMiembroId(5L)).thenReturn(List.of(inscripcion(2L, pilates, "cancelada")));

        assertNotNull(inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, 5L), null));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si la clase no tiene cupo")
    void inscribir_SinCupo() {
        when(inscripcionRepository.countByClaseIdAndEstado(10L, "activa")).thenReturn(2L);

        assertThrows(ConflictException.class,
                () -> inscripcionService.inscribirMiembro(new InscripcionRequestDTO(10L, 5L), null));
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cambiar de clase sin chocar con la clase que se libera")
    void cambiarClase_Exito() {
        Inscripcion actual = inscripcion(1L, yoga, "activa");
        Clase pilates = clase(11L, "Pilates", List.of("Lunes"), "08:00", "09:00", 10);
        when(inscripcionRepository.findByIdAndEstado(1L, "activa")).thenReturn(Optional.of(actual));
        when(claseRepository.findById(11L)).thenReturn(Optional.of(pilates));
        when(inscripcionRepository.findByMiembroId(5L)).thenReturn(List.of(actual));
        when(inscripcionRepository.countByClaseIdAndEstado(11L, "activa")).thenReturn(3L);

        InscripcionUpdateDTO dto = new InscripcionUpdateDTO();
        dto.setNuevaClaseId(11L);
        InscripcionResponseDTO resultado = inscripcionService.cambiarClase(1L, dto, null);

        assertEquals(11L, resultado.getClaseId());
        assertEquals(6, resultado.getCupoDisponibleRestante());
    }

    @Test
    @DisplayName("Debe rechazar el cambio a una clase sin cupo o inactiva")
    void cambiarClase_Rechazos() {
        Inscripcion actual = inscripcion(1L, yoga, "activa");
        Clase llena = clase(12L, "Pilates", List.of("Viernes"), "08:00", "09:00", 1);
        Clase cerrada = clase(13L, "Pilates", List.of("Viernes"), "10:00", "11:00", 10);
        cerrada.setEstado("CERRADA");
        when(inscripcionRepository.findByIdAndEstado(1L, "activa")).thenReturn(Optional.of(actual));
        when(claseRepository.findById(12L)).thenReturn(Optional.of(llena));
        when(claseRepository.findById(13L)).thenReturn(Optional.of(cerrada));
        when(inscripcionRepository.countByClaseIdAndEstado(12L, "activa")).thenReturn(1L);

        InscripcionUpdateDTO aLlena = new InscripcionUpdateDTO();
        aLlena.setNuevaClaseId(12L);
        InscripcionUpdateDTO aCerrada = new InscripcionUpdateDTO();
        aCerrada.setNuevaClaseId(13L);

        assertThrows(ConflictException.class, () -> inscripcionService.cambiarClase(1L, aLlena, null));
        assertThrows(ConflictException.class, () -> inscripcionService.cambiarClase(1L, aCerrada, null));
    }

    @Test
    @DisplayName("Debe consultar inscripciones por miembro, por clase o todas")
    void obtenerInscripciones() {
        Inscripcion i = inscripcion(1L, yoga, "activa");
        when(inscripcionRepository.findByMiembroId(5L)).thenReturn(List.of(i));
        when(inscripcionRepository.findByClaseId(10L)).thenReturn(List.of(i));
        when(inscripcionRepository.findAll()).thenReturn(List.of(i, i));
        when(inscripcionRepository.countByClaseIdAndEstado(10L, "activa")).thenReturn(1L);

        assertEquals(1, inscripcionService.obtenerInscripciones(5L, null).size());
        assertEquals(1, inscripcionService.obtenerInscripciones(null, 10L).size());
        assertEquals(2, inscripcionService.obtenerInscripciones(null, null).size());
        assertEquals(1, inscripcionService.obtenerInscripciones(null, null).get(0).getCupoDisponibleRestante());
    }

    @Test
    @DisplayName("Debe obtener el detalle y cancelar una inscripción")
    void obtenerYCancelar() {
        Inscripcion i = inscripcion(1L, yoga, "activa");
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(i));

        assertEquals("Yoga", inscripcionService.obtenerPorId(1L).getTipoClase());

        inscripcionService.cancelarInscripcion(1L, null);
        verify(inscripcionRepository).delete(i);
    }
}
