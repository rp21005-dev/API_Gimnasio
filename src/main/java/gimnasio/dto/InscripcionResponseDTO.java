package gimnasio.dto;

import java.time.LocalDate;

public class InscripcionResponseDTO {

    private Long id;
    private Long miembroId;
    private String nombreMiembro;
    private Long claseId;
    private String tipoClase;
    private LocalDate fechaInscripcion;
    private String estado;
    private Integer cupoDisponibleRestante;

    public InscripcionResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMiembroId() {
        return miembroId;
    }

    public void setMiembroId(Long miembroId) {
        this.miembroId = miembroId;
    }

    public String getNombreMiembro() {
        return nombreMiembro;
    }

    public void setNombreMiembro(String nombreMiembro) {
        this.nombreMiembro = nombreMiembro;
    }

    public Long getClaseId() {
        return claseId;
    }

    public void setClaseId(Long claseId) {
        this.claseId = claseId;
    }

    public String getTipoClase() {
        return tipoClase;
    }

    public void setTipoClase(String tipoClase) {
        this.tipoClase = tipoClase;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCupoDisponibleRestante() {
        return cupoDisponibleRestante;
    }

    public void setCupoDisponibleRestante(Integer cupoDisponibleRestante) {
        this.cupoDisponibleRestante = cupoDisponibleRestante;
    }
}
