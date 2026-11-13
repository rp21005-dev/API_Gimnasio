package gimnasio.dto;

public class InscripcionRequestDTO {

    private Long claseId;
    private Long miembroId; // Opcional (utilizado si el Administrador realiza la inscripción)

    public InscripcionRequestDTO() {
    }

    public InscripcionRequestDTO(Long claseId, Long miembroId) {
        this.claseId = claseId;
        this.miembroId = miembroId;
    }

    public Long getClaseId() {
        return claseId;
    }

    public void setClaseId(Long claseId) {
        this.claseId = claseId;
    }

    public Long getMiembroId() {
        return miembroId;
    }

    public void setMiembroId(Long miembroId) {
        this.miembroId = miembroId;
    }
}
