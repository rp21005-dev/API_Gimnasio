package gimnasio.repositoy;

import gimnasio.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByMiembroId(Long miembroId);

    List<Inscripcion> findByClaseId(Long claseId);

    boolean existsByMiembroIdAndClaseIdAndEstado(Long miembroId, Long claseId, String estado);

    long countByClaseIdAndEstado(Long claseId, String estado);

    Optional<Inscripcion> findByIdAndEstado(Long id, String estado);
}