package com.appasistencia.dtos.response;

import com.appasistencia.models.UsuarioAlumno;
import java.time.LocalDateTime;

public class UsuarioAlumnoResponseDTO {

    private Long idAlumno;
    private Long idUsuario;
    private String legajo;
    private Double promedio;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public UsuarioAlumnoResponseDTO() {}

    public static UsuarioAlumnoResponseDTO fromEntity(UsuarioAlumno alumno) {
        UsuarioAlumnoResponseDTO dto = new UsuarioAlumnoResponseDTO();
        dto.idAlumno = alumno.getIdAlumno();
        if (alumno.getUsuario() != null) {
            dto.idUsuario = alumno.getUsuario().getIdUsuario();
        }
        dto.legajo = alumno.getLegajo();
        dto.promedio = alumno.getPromedio();
        dto.fechaCreacion = alumno.getFechaCreacion();
        dto.activo = alumno.isActivo();
        return dto;
    }

    // Getters
    public Long getIdAlumno() { return idAlumno; }
    public Long getIdUsuario() { return idUsuario; }
    public String getLegajo() { return legajo; }
    public Double getPromedio() { return promedio; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
