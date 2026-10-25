package gimnasio.dto;

public class AsistenciaUpdateDTO {

    private String estado; // "PRESENTE" o "AUSENTE"
    private String observacion;

    public AsistenciaUpdateDTO() {
    }

    public AsistenciaUpdateDTO(String estado, String observacion) {
        this.estado = estado;
        this.observacion = observacion;
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