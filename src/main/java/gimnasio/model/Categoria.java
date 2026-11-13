package gimnasio.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false)
    private String objetivoPrincipal;

    // Lista de tipos de clase permitidos
    @ElementCollection
    @CollectionTable(name = "categoria_tipos_clase", joinColumns = @JoinColumn(name = "categoria_id"))
    @Column(name = "tipo_clase", nullable = false, length = 80)
    private List<String> tiposClase = new ArrayList<>();
}
