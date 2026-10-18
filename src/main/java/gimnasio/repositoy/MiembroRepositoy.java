package gimnasio.repositoy;

import gimnasio.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MiembroRepositoy extends JpaRepository<Miembro, Long> {
}
