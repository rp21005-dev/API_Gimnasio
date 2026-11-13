package gimnasio.repository;

import gimnasio.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {

    // Para validar traslapes
    List<Clase> findByEntrenadorIdAndEstado(Long entrenadorId, String estado);

    List<Clase> findBySalaIdAndEstado(Long salaId, String estado);

    // Para impedir eliminar categorías, salas o entrenadores con clases activas
    boolean existsByCategoriaIdAndEstado(Long categoriaId, String estado);

    boolean existsBySalaIdAndEstado(Long salaId, String estado);

    boolean existsByEntrenadorIdAndEstado(Long entrenadorId, String estado);
}
