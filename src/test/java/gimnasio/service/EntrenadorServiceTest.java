package gimnasio.service;

import gimnasio.dto.EntrenadorRequestDTO;
import gimnasio.dto.EntrenadorResponseDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Entrenador;
import gimnasio.repository.EntrenadorRepository;
import gimnasio.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prueba unitaria: no levanta Spring ni usa la base de datos, el repositorio es simulado
@ExtendWith(MockitoExtension.class)
class EntrenadorServiceTest {

    @Mock
    private EntrenadorRepository entrenadorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EntrenadorService entrenadorService;

    private Entrenador entrenador;
    private EntrenadorRequestDTO dto;

    @BeforeEach
    void setUp() {
        entrenador = new Entrenador("Ana", "Prueba", "7777-0000", "Yoga");
        entrenador.setId(1L);
        entrenador.setCorreo("ana@ejemplo.com");
        entrenador.setEstado("ACTIVO");

        dto = new EntrenadorRequestDTO("Ana", "Prueba", "ana@ejemplo.com", "7777-0000", "Yoga", "clave1234");
    }

    @Test
    @DisplayName("Debe obtener un entrenador por ID exitosamente")
    void obtenerPorId_Exito() {
        // Arrange (Preparar)
        when(entrenadorRepository.findById(1L)).thenReturn(Optional.of(entrenador));

        // Act (Ejecutar)
        EntrenadorResponseDTO resultado = entrenadorService.obtenerPorId(1L);

        // Assert (Verificar)
        assertEquals(1L, resultado.getId());
        assertEquals("Ana Prueba", resultado.getNombreCompleto());
        verify(entrenadorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException si el ID no existe")
    void obtenerPorId_NoEncontrado() {
        when(entrenadorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> entrenadorService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Debe guardar la contraseña encriptada al crear un entrenador")
    void crearEntrenador_Exito() {
        when(usuarioRepository.existsByCorreo("ana@ejemplo.com")).thenReturn(false);
        when(passwordEncoder.encode("clave1234")).thenReturn("hash-simulado");
        when(entrenadorRepository.save(any(Entrenador.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        EntrenadorResponseDTO resultado = entrenadorService.crearEntrenador(dto);

        assertEquals("ACTIVO", resultado.getEstado());
        verify(entrenadorRepository).save(argThat(guardado -> "hash-simulado".equals(guardado.getContrasenaHash())));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el correo ya existe")
    void crearEntrenador_CorreoRepetido() {
        when(usuarioRepository.existsByCorreo("ana@ejemplo.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> entrenadorService.crearEntrenador(dto));
        verify(entrenadorRepository, never()).save(any());
    }
}
