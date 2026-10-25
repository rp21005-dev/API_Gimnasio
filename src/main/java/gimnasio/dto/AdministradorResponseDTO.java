package gimnasio.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AdministradorResponseDTO {
    private Long id;
    private String correo;
    private String estado;
}
