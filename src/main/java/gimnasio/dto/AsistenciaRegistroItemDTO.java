package gimnasio.dto;

public class AsistenciaRegistroItemDTO {

    private Long miembroId;
    private String estado; // "PRESENTE" o "AUSENTE"
    private String observacion;

    public AsistenciaRegistroItemDTO() {
    }

    public AsistenciaRegistroItemDTO(Long miembroId, String estado, String observacion) {
        this.miembroId = miembroId;
        this.estado = estado;
        this.observacion = observacion;
    }

    public Long getMiembroId() {
        return miembroId;
    }

    public void setMiembroId(Long miembroId) {
        this.miembroId = miembroId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
