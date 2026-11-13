package gimnasio.dto;

import java.time.LocalDate;
import java.util.List;

public class AsistenciaRequestDTO {

    private Long claseId;
    private LocalDate fechaSesion;
    private List<AsistenciaRegistroItemDTO> registros;

    public AsistenciaRequestDTO() {
    }

    public Long getClaseId() {
        return claseId;
    }

    public void setClaseId(Long claseId) {
        this.claseId = claseId;
    }

    public LocalDate getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(LocalDate fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public List<AsistenciaRegistroItemDTO> getRegistros() {
        return registros;
    }

    public void setRegistros(List<AsistenciaRegistroItemDTO> registros) {
        this.registros = registros;
    }
}
