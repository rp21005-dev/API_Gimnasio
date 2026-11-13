package gimnasio.service;

import gimnasio.dto.InscripcionRequestDTO;
import gimnasio.dto.InscripcionResponseDTO;
import gimnasio.dto.InscripcionUpdateDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Clase;
import gimnasio.model.Inscripcion;
import gimnasio.model.Miembro;
import gimnasio.repositoy.ClaseRepository;
import gimnasio.repositoy.InscripcionRepository;
import gimnasio.repositoy.MiembroRepositoy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private MiembroRepositoy miembroRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Transactional
    public InscripcionResponseDTO inscribirMiembro(InscripcionRequestDTO requestDTO, Long usuarioId) {
        Long miembroId = (requestDTO.getMiembroId() != null) ? requestDTO.getMiembroId() : usuarioId;

        if (miembroId == null) {
            throw new IllegalArgumentException("El ID del miembro es requerido.");
        }

        // 1. Validar que el miembro exista y esté activo (CU-06 Paso 5)
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new RuntimeException("Miembro no encontrado con ID: " + miembroId));

        if (!"activo".equalsIgnoreCase(miembro.getEstado())) {
            throw new ConflictException("El miembro no está activo.");
        }

        // 2. Validar que la clase exista y esté activa (CU-06 Paso 6)
        Clase claseDestino = claseRepository.findById(requestDTO.getClaseId())
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + requestDTO.getClaseId()));

        if (!"activa".equalsIgnoreCase(claseDestino.getEstado())) {
            throw new ConflictException("La clase está cerrada o inactiva.");
        }

        // 3. Validar duplicidad de inscripción (CU-06 Paso 7)
        boolean yaInscrito = inscripcionRepository.existsByMiembroIdAndClaseIdAndEstado(
                miembroId, requestDTO.getClaseId(), "activa");
        if (yaInscrito) {
            throw new ConflictException("Ya está inscrito en esta clase.");
        }

        // 4. Validar traslape de horario con otras clases activas del miembro (CU-06 Paso 8)
        validarTraslapeDeHorario(miembroId, claseDestino, null);

        // 5. Verificar y reservar cupo en la clase (CU-07 / CU-06 Paso 9)
        long inscritosActuales = inscripcionRepository.countByClaseIdAndEstado(claseDestino.getId(), "activa");
        if (inscritosActuales >= claseDestino.getCupoMaximo()) {
            throw new ConflictException("La clase no tiene cupo disponible.");
        }

        // 6. Guardar la inscripción
        Inscripcion nuevaInscripcion = new Inscripcion();
        nuevaInscripcion.setMiembro(miembro);
        nuevaInscripcion.setClase(claseDestino);
        nuevaInscripcion.setFechaInscripcion(LocalDate.now());
        nuevaInscripcion.setEstado("activa");

        Inscripcion guardada = inscripcionRepository.save(nuevaInscripcion);

        int cupoDisponibleRestante = claseDestino.getCupoMaximo() - (int) (inscritosActuales + 1);
        return mapearADTO(guardada, cupoDisponibleRestante);
    }

    @Transactional
    public InscripcionResponseDTO cambiarClase(Long id, InscripcionUpdateDTO updateDTO, Long usuarioId) {
        Inscripcion inscripcionActual = inscripcionRepository.findByIdAndEstado(id, "activa")
                .orElseThrow(() -> new RuntimeException("Inscripción activa no encontrada con ID: " + id));

        Clase nuevaClase = claseRepository.findById(updateDTO.getNuevaClaseId())
                .orElseThrow(() -> new RuntimeException("Nueva clase no encontrada con ID: " + updateDTO.getNuevaClaseId()));

        if (!"activa".equalsIgnoreCase(nuevaClase.getEstado())) {
            throw new ConflictException("La nueva clase seleccionada está inactiva.");
        }

        // Validar traslape excluyendo la clase de origen que se está liberando
        validarTraslapeDeHorario(inscripcionActual.getMiembro().getId(), nuevaClase, inscripcionActual.getClase().getId());

        // Verificar cupo disponible en la nueva clase
        long inscritosNuevaClase = inscripcionRepository.countByClaseIdAndEstado(nuevaClase.getId(), "activa");
        if (inscritosNuevaClase >= nuevaClase.getCupoMaximo()) {
            throw new ConflictException("La nueva clase no tiene cupo disponible.");
        }

        // Actualizar la inscripción
        inscripcionActual.setClase(nuevaClase);
        inscripcionActual.setFechaInscripcion(LocalDate.now());

        Inscripcion actualizada = inscripcionRepository.save(inscripcionActual);
        int cupoRestante = nuevaClase.getCupoMaximo() - (int) (inscritosNuevaClase + 1);

        return mapearADTO(actualizada, cupoRestante);
    }

    @Transactional
    public void cancelarInscripcion(Long id, Long usuarioId) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada con ID: " + id));

        inscripcionRepository.delete(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> obtenerInscripciones(Long miembroId, Long claseId) {
        List<Inscripcion> lista;
        if (miembroId != null) {
            lista = inscripcionRepository.findByMiembroId(miembroId);
        } else if (claseId != null) {
            lista = inscripcionRepository.findByClaseId(claseId);
        } else {
            lista = inscripcionRepository.findAll();
        }

        return lista.stream()
                .map(i -> mapearADTO(i, calcularCupoDisponible(i.getClase())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InscripcionResponseDTO obtenerPorId(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada con ID: " + id));
        return mapearADTO(inscripcion, calcularCupoDisponible(inscripcion.getClase()));
    }

    private void validarTraslapeDeHorario(Long miembroId, Clase claseNueva, Long claseIdAExcluir) {
        List<Inscripcion> inscripcionesActivas = inscripcionRepository.findByMiembroId(miembroId);

        for (Inscripcion inscripcion : inscripcionesActivas) {
            if (!"activa".equalsIgnoreCase(inscripcion.getEstado())) {
                continue;
            }

            Clase claseInscrita = inscripcion.getClase();

            if (claseIdAExcluir != null && claseInscrita.getId().equals(claseIdAExcluir)) {
                continue;
            }

            boolean compartenDia = tienenDiasEnComun(claseNueva.getDiasSemana(), claseInscrita.getDiasSemana());

            if (compartenDia) {
                boolean horasSeSeTraslapan = haySolapamientoHorario(
                        claseNueva.getHoraInicio(), claseNueva.getHoraFin(),
                        claseInscrita.getHoraInicio(), claseInscrita.getHoraFin()
                );

                if (horasSeSeTraslapan) {
                    throw new ConflictException(String.format(
                            "Existe un traslape de horario con la clase '%s' en el día/horario seleccionado.",
                            claseInscrita.getTipoClase()
                    ));
                }
            }
        }
    }

    private boolean tienenDiasEnComun(List<String> diasClaseA, List<String> diasClaseB) {
        if (diasClaseA == null || diasClaseB == null) return false;
        return diasClaseA.stream().anyMatch(diasClaseB::contains);
    }

    private boolean haySolapamientoHorario(LocalTime inicioA, LocalTime finA, LocalTime inicioB, LocalTime finB) {
        return inicioA.isBefore(finB) && inicioB.isBefore(finA);
    }

    private int calcularCupoDisponible(Clase clase) {
        if (clase == null) return 0;
        long inscritos = inscripcionRepository.countByClaseIdAndEstado(clase.getId(), "activa");
        return Math.max(0, clase.getCupoMaximo() - (int) inscritos);
    }

    private InscripcionResponseDTO mapearADTO(Inscripcion inscripcion, int cupoRestante) {
        InscripcionResponseDTO dto = new InscripcionResponseDTO();
        dto.setId(inscripcion.getId());

        if (inscripcion.getMiembro() != null) {
            dto.setMiembroId(inscripcion.getMiembro().getId());
            dto.setNombreMiembro(inscripcion.getMiembro().getCorreo());
        }

        if (inscripcion.getClase() != null) {
            dto.setClaseId(inscripcion.getClase().getId());
            dto.setTipoClase(inscripcion.getClase().getTipoClase());
        }

        dto.setFechaInscripcion(inscripcion.getFechaInscripcion());
        dto.setEstado(inscripcion.getEstado());
        dto.setCupoDisponibleRestante(cupoRestante);
        return dto;
    }
}