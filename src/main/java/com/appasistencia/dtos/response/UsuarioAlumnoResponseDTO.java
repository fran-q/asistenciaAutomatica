package com.appasistencia.dtos.response;

import com.appasistencia.models.UsuarioAlumno;
import java.time.LocalDateTime;

// DTO de respuesta: perfil de alumno (vinculado a un usuario via idUsuario)
public class UsuarioAlumnoResponseDTO {

    private Long idAlumno;
    // Relacion (ID plano) - referencia al usuario base
    private Long idUsuario;
    // Datos academicos
    private String legajo;
    private Double promedio;
    // Campos de auditoria
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public UsuarioAlumnoResponseDTO() {}

    // Conversion desde entidad - extrae idUsuario como ID plano
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
