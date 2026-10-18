package gimnasio.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorResponseDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String especialidad;
    private String estado;
}
