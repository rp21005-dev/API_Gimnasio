package gimnasio.service;

import gimnasio.dto.AsistenciaRegistroItemDTO;
import gimnasio.dto.AsistenciaRequestDTO;
import gimnasio.dto.AsistenciaResponseDTO;
import gimnasio.dto.AsistenciaUpdateDTO;
import gimnasio.dto.HojaAsistenciaResponseDTO;
import gimnasio.exception.ConflictException;
import gimnasio.model.Asistencia;
import gimnasio.model.Clase;
import gimnasio.model.Inscripcion;
import gimnasio.repositoy.AsistenciaRepository;
import gimnasio.repositoy.ClaseRepository;
import gimnasio.repositoy.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Transactional
    public HojaAsistenciaResponseDTO registrarAsistencia(AsistenciaRequestDTO requestDTO, Long usuarioId) {
        if (requestDTO.getClaseId() == null || requestDTO.getFechaSesion() == null || requestDTO.getRegistros() == null) {
            throw new IllegalArgumentException("La clase, fecha de sesión y la lista de registros son obligatorias.");
        }

        Clase clase = claseRepository.findById(requestDTO.getClaseId())
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + requestDTO.getClaseId()));

        if (!"activa".equalsIgnoreCase(clase.getEstado())) {
            throw new ConflictException("La clase está cerrada o inactiva.");
        }

        List<Inscripcion> inscripcionesClase = inscripcionRepository.findByClaseId(requestDTO.getClaseId());
        List<Asistencia> asistenciasGuardadas = new ArrayList<>();

        for (AsistenciaRegistroItemDTO item : requestDTO.getRegistros()) {
            Inscripcion inscripcionMiembro = inscripcionesClase.stream()
                    .filter(i -> i.getMiembro().getId().equals(item.getMiembroId()) && "activa".equalsIgnoreCase(i.getEstado()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("El miembro con ID " + item.getMiembroId() + " no está inscrito en esta clase."));

            if (inscripcionMiembro.getFechaInscripcion().isAfter(requestDTO.getFechaSesion())) {
                throw new IllegalArgumentException("El miembro con ID " + item.getMiembroId() + " se inscribió con posterioridad a esta sesión.");
            }

            boolean yaExiste = asistenciaRepository.existsByInscripcionIdAndFechaSesion(inscripcionMiembro.getId(), requestDTO.getFechaSesion());
            if (yaExiste) {
                throw new ConflictException("Ya existe un registro de asistencia para el miembro ID " + item.getMiembroId() + " en la sesión " + requestDTO.getFechaSesion());
            }

            Asistencia nuevaAsistencia = new Asistencia();
            nuevaAsistencia.setInscripcion(inscripcionMiembro);
            nuevaAsistencia.setFechaSesion(requestDTO.getFechaSesion());
            nuevaAsistencia.setEstado(item.getEstado().toUpperCase());
            nuevaAsistencia.setObservacion(item.getObservacion());

            asistenciasGuardadas.add(asistenciaRepository.save(nuevaAsistencia));
        }

        return construirHojaResumen(clase, requestDTO.getFechaSesion(), asistenciasGuardadas);
    }

    @Transactional(readOnly = true)
    public HojaAsistenciaResponseDTO obtenerHojaAsistencia(Long claseId, LocalDate fechaSesion) {
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + claseId));

        List<Asistencia> asistencias = asistenciaRepository.findByInscripcionClaseIdAndFechaSesion(claseId, fechaSesion);
        return construirHojaResumen(clase, fechaSesion, asistencias);
    }

    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> obtenerAsistencias(Long miembroId, Long claseId) {
        List<Asistencia> lista;
        if (miembroId != null) {
            lista = asistenciaRepository.findByInscripcionMiembroId(miembroId);
        } else if (claseId != null) {
            lista = asistenciaRepository.findByInscripcionClaseId(claseId);
        } else {
            lista = asistenciaRepository.findAll();
        }

        return lista.stream().map(this::mapearADTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AsistenciaResponseDTO obtenerPorId(Long id) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado con ID: " + id));
        return mapearADTO(asistencia);
    }

    @Transactional
    public AsistenciaResponseDTO corregirAsistencia(Long id, AsistenciaUpdateDTO updateDTO, Long usuarioId) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado con ID: " + id));

        if (!"activa".equalsIgnoreCase(asistencia.getInscripcion().getEstado())) {
            throw new ConflictException("El historial de asistencia es de solo lectura porque el miembro ya no está inscrito.");
        }

        if (updateDTO.getEstado() != null) {
            asistencia.setEstado(updateDTO.getEstado().toUpperCase());
        }
        if (updateDTO.getObservacion() != null) {
            asistencia.setObservacion(updateDTO.getObservacion());
        }

        Asistencia actualizada = asistenciaRepository.save(asistencia);
        return mapearADTO(actualizada);
    }

    @Transactional
    public void eliminarAsistencia(Long id, Long usuarioId) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado con ID: " + id));

        if (!"activa".equalsIgnoreCase(asistencia.getInscripcion().getEstado())) {
            throw new ConflictException("El historial de asistencia es de solo lectura.");
        }

        asistenciaRepository.delete(asistencia);
    }

    private HojaAsistenciaResponseDTO construirHojaResumen(Clase clase, LocalDate fechaSesion, List<Asistencia> asistencias) {
        HojaAsistenciaResponseDTO hoja = new HojaAsistenciaResponseDTO();
        hoja.setClaseId(clase.getId());
        hoja.setTipoClase(clase.getTipoClase());
        hoja.setFechaSesion(fechaSesion);

        int presentes = (int) asistencias.stream().filter(a -> "PRESENTE".equalsIgnoreCase(a.getEstado())).count();
        int ausentes = (int) asistencias.stream().filter(a -> "AUSENTE".equalsIgnoreCase(a.getEstado())).count();
        long inscritosTotales = inscripcionRepository.countByClaseIdAndEstado(clase.getId(), "activa");

        hoja.setPresentes(presentes);
        hoja.setAusentes(ausentes);
        hoja.setSinRegistro((int) Math.max(0, inscritosTotales - asistencias.size()));

        List<AsistenciaResponseDTO> dtoList = asistencias.stream().map(this::mapearADTO).collect(Collectors.toList());
        hoja.setRegistros(dtoList);

        return hoja;
    }

    private AsistenciaResponseDTO mapearADTO(Asistencia asistencia) {
        AsistenciaResponseDTO dto = new AsistenciaResponseDTO();
        dto.setId(asistencia.getId());
        dto.setFechaSesion(asistencia.getFechaSesion());
        dto.setEstado(asistencia.getEstado());
        dto.setObservacion(asistencia.getObservacion());

        if (asistencia.getInscripcion() != null) {
            dto.setInscripcionId(asistencia.getInscripcion().getId());
            if (asistencia.getInscripcion().getMiembro() != null) {
                dto.setMiembroId(asistencia.getInscripcion().getMiembro().getId());
                dto.setNombreMiembro(asistencia.getInscripcion().getMiembro().getCorreo());
            }
            if (asistencia.getInscripcion().getClase() != null) {
                dto.setClaseId(asistencia.getInscripcion().getClase().getId());
                dto.setTipoClase(asistencia.getInscripcion().getClase().getTipoClase());
            }
        }
        return dto;
    }
}