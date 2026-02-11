package com.appasistencia.dtos.response;

import com.appasistencia.models.Institucion;
import java.time.LocalDateTime;

public class InstitucionResponseDTO {

    private Long idInstitucion;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public InstitucionResponseDTO() {}

    public static InstitucionResponseDTO fromEntity(Institucion institucion) {
        InstitucionResponseDTO dto = new InstitucionResponseDTO();
        dto.idInstitucion = institucion.getIdInstitucion();
        dto.nombre = institucion.getNombre();
        dto.direccion = institucion.getDireccion();
        dto.telefono = institucion.getTelefono();
        dto.email = institucion.getEmail();
        dto.fechaCreacion = institucion.getFechaCreacion();
        dto.activo = institucion.isActivo();
        return dto;
    }

    // Getters
    public Long getIdInstitucion() { return idInstitucion; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
