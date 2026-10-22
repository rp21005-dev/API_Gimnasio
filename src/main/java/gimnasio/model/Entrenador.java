package gimnasio.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor //Constructor vacio
@AllArgsConstructor // Contructo con los argumentos
@Entity
@Table(name = "entrenadores")
public class Entrenador extends Usuario{
    private String nombres;
    private String apellidos;
    private String telefono;
    private String especialidad;

    /// Metodo para obtener nombre propio
    public String getNombreCompleto(){
        return  this.nombres + " " + this.apellidos;
    }
}
