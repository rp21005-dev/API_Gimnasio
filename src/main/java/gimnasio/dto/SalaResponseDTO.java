package gimnasio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String ubicacion;
    private int capacidadMaxima;
    private String descripcion;
}
