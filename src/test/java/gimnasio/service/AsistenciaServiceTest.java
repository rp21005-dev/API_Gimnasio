package gimnasio.service;

import gimnasio.dto.AsistenciaRegistroItemDTO;
import gimnasio.dto.AsistenciaRequestDTO;
import gimnasio.dto.AsistenciaUpdateDTO;
import gimnasio.dto.HojaAsistenciaResponseDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Asistencia;
import gimnasio.model.Clase;
import gimnasio.model.Inscripcion;
import gimnasio.model.Miembro;
import gimnasio.repository.ClaseRepository;
import gimnasio.repository.AsistenciaRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prueba unitaria del registro de asistencia por sesión
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // El setUp prepara datos que no todas las pruebas usan
class AsistenciaServiceTest {

    private static final LocalDate SESION = LocalDate.of(2026, 11, 16);

    @Mock
    private AsistenciaRepository asistenciaRepository;
    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private ClaseRepository claseRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private Clase clase;
    private Inscripcion inscripcionAna;
    private Inscripcion inscripcionLuis;

    @BeforeEach
    void setUp() {
        clase = new Clase();
        clase.setId(10L);
        clase.setTipoClase("Yoga");
        clase.setEstado("ACTIVA");

        inscripcionAna = inscripcion(1L, miembro(5L, "ana@ejemplo.com"), SESION.minusDays(7));
        inscripcionLuis = inscripcion(2L, miembro(6L, "luis@ejemplo.com"), SESION.minusDays(3));

        when(claseRepository.findById(10L)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.findByClaseId(10L)).thenReturn(List.of(inscripcionAna, inscripcionLuis));
        when(inscripcionRepository.countByClaseIdAndEstado(10L, "activa")).thenReturn(3L);
        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Miembro miembro(Long id, String correo) {
        Miembro m = new Miembro();
        m.setId(id);
        m.setCorreo(correo);
        return m;
    }

    private Inscripcion inscripcion(Long id, Miembro miembro, LocalDate fecha) {
        Inscripcion i = new Inscripcion(miembro, clase, fecha, "activa");
        i.setId(id);
        return i;
    }

    private AsistenciaRequestDTO request(AsistenciaRegistroItemDTO... registros) {
        AsistenciaRequestDTO dto = new AsistenciaRequestDTO();
        dto.setClaseId(10L);
        dto.setFechaSesion(SESION);
        dto.setRegistros(List.of(registros));
        return dto;
    }

    @Test
    @DisplayName("Debe registrar la asistencia y devolver el resumen de la sesión")
    void registrar_Exito() {
        HojaAsistenciaResponseDTO hoja = asistenciaService.registrarAsistencia(request(
                new AsistenciaRegistroItemDTO(5L, "presente", null),
                new AsistenciaRegistroItemDTO(6L, "AUSENTE", "Avisó por enfermedad")), null);

        assertEquals(10L, hoja.getClaseId());
        assertEquals(1, hoja.getPresentes());
        assertEquals(1, hoja.getAusentes());
        assertEquals(1, hoja.getSinRegistro()); // 3 inscritos activos, 2 registrados
        assertEquals("PRESENTE", hoja.getRegistros().get(0).getEstado()); // se guarda en mayúsculas
        verify(asistenciaRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Debe rechazar una solicitud sin clase, fecha o registros")
    void registrar_DatosIncompletos() {
        assertThrows(IllegalArgumentException.class,
                () -> asistenciaService.registrarAsistencia(new AsistenciaRequestDTO(), null));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si la clase está cerrada")
    void registrar_ClaseCerrada() {
        clase.setEstado("CERRADA");

        assertThrows(ConflictException.class, () -> asistenciaService.registrarAsistencia(
                request(new AsistenciaRegistroItemDTO(5L, "PRESENTE", null)), null));
    }

    @Test
    @DisplayName("Debe rechazar a un miembro que no está inscrito en la clase")
    void registrar_MiembroNoInscrito() {
        assertThrows(IllegalArgumentException.class, () -> asistenciaService.registrarAsistencia(
                request(new AsistenciaRegistroItemDTO(99L, "PRESENTE", null)), null));
    }

    @Test
    @DisplayName("Debe rechazar una sesión anterior a la fecha de inscripción")
    void registrar_SesionAnteriorALaInscripcion() {
        inscripcionLuis.setFechaInscripcion(SESION.plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> asistenciaService.registrarAsistencia(
                request(new AsistenciaRegistroItemDTO(6L, "PRESENTE", null)), null));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si ya hay asistencia registrada en esa sesión")
    void registrar_Duplicado() {
        when(asistenciaRepository.existsByInscripcionIdAndFechaSesion(1L, SESION)).thenReturn(true);

        assertThrows(ConflictException.class, () -> asistenciaService.registrarAsistencia(
                request(new AsistenciaRegistroItemDTO(5L, "PRESENTE", null)), null));
    }

    @Test
    @DisplayName("Debe obtener la hoja de asistencia de una sesión")
    void obtenerHoja() {
        Asistencia a = new Asistencia(inscripcionAna, SESION, "PRESENTE", null);
        when(asistenciaRepository.findByInscripcionClaseIdAndFechaSesion(10L, SESION)).thenReturn(List.of(a));

        HojaAsistenciaResponseDTO hoja = asistenciaService.obtenerHojaAsistencia(10L, SESION);

        assertEquals(1, hoja.getPresentes());
        assertEquals(2, hoja.getSinRegistro());
        assertEquals("ana@ejemplo.com", hoja.getRegistros().get(0).getNombreMiembro());
    }

    @Test
    @DisplayName("Debe consultar asistencias por miembro, por clase o todas, y por ID")
    void consultar() {
        Asistencia a = new Asistencia(inscripcionAna, SESION, "PRESENTE", null);
        a.setId(7L);
        when(asistenciaRepository.findByInscripcionMiembroId(5L)).thenReturn(List.of(a));
        when(asistenciaRepository.findByInscripcionClaseId(10L)).thenReturn(List.of(a));
        when(asistenciaRepository.findAll()).thenReturn(List.of(a));
        when(asistenciaRepository.findById(7L)).thenReturn(Optional.of(a));

        assertEquals(1, asistenciaService.obtenerAsistencias(5L, null).size());
        assertEquals(1, asistenciaService.obtenerAsistencias(null, 10L).size());
        assertEquals(1, asistenciaService.obtenerAsistencias(null, null).size());
        assertEquals(10L, asistenciaService.obtenerPorId(7L).getClaseId());
    }

    @Test
    @DisplayName("Debe corregir el estado y la observación de una asistencia")
    void corregir_Exito() {
        Asistencia a = new Asistencia(inscripcionAna, SESION, "AUSENTE", null);
        when(asistenciaRepository.findById(7L)).thenReturn(Optional.of(a));
        AsistenciaUpdateDTO dto = new AsistenciaUpdateDTO();
        dto.setEstado("presente");
        dto.setObservacion("Llegó tarde");

        assertEquals("PRESENTE", asistenciaService.corregirAsistencia(7L, dto, null).getEstado());
        assertEquals("Llegó tarde", a.getObservacion());
    }

    @Test
    @DisplayName("El historial es de solo lectura si la inscripción ya no está activa")
    void historialSoloLectura() {
        inscripcionAna.setEstado("cancelada");
        Asistencia a = new Asistencia(inscripcionAna, SESION, "PRESENTE", null);
        when(asistenciaRepository.findById(7L)).thenReturn(Optional.of(a));

        assertThrows(ConflictException.class,
                () -> asistenciaService.corregirAsistencia(7L, new AsistenciaUpdateDTO(), null));
        assertThrows(ConflictException.class, () -> asistenciaService.eliminarAsistencia(7L, null));
        verify(asistenciaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debe eliminar una asistencia de una inscripción activa")
    void eliminar_Exito() {
        Asistencia a = new Asistencia(inscripcionAna, SESION, "PRESENTE", null);
        when(asistenciaRepository.findById(7L)).thenReturn(Optional.of(a));

        asistenciaService.eliminarAsistencia(7L, null);

        verify(asistenciaRepository).delete(a);
    }
}
