package gimnasio.model;
import  lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class Miembro  extends Usuario{
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private String telefono;
    private LocalDate fechaNacimiento;
    private LocalDate fechaRegistro;

}
