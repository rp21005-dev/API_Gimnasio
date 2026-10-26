package gimnasio.model;
import jakarta.persistence.*;
import  lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/// me genera los getters y setter de la clase
@Data
@AllArgsConstructor /// me genera el contructo con todos sus argumentos
@NoArgsConstructor /// Genera un contructor vacio en caso que lo ocupe mas adelante
@Entity
@Inheritance(strategy = InheritanceType.JOINED) /// Los campos se heredan a la tabla de cada subclase
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasenaHash;

    private String estado;
}
