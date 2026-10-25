package gimnasio.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import  lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "miembros")
public class Miembro  extends Usuario{
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private String telefono;
    private LocalDate fechaNacimiento;
    private LocalDate fechaRegistro;

}
