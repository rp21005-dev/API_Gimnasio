package gimnasio.repositoy;

import gimnasio.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByInscripcionMiembroId(Long miembroId);

    List<Asistencia> findByInscripcionClaseId(Long claseId);

    List<Asistencia> findByInscripcionClaseIdAndFechaSesion(Long claseId, LocalDate fechaSesion);

    Optional<Asistencia> findByInscripcionIdAndFechaSesion(Long inscripcionId, LocalDate fechaSesion);

    boolean existsByInscripcionIdAndFechaSesion(Long inscripcionId, LocalDate fechaSesion);
}

