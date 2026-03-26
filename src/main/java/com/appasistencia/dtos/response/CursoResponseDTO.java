package com.appasistencia.dtos.response;

import com.appasistencia.models.Curso;
import com.appasistencia.models.Turno;
import java.time.LocalDateTime;

// DTO de respuesta: datos de un curso (division de carrera en un anio lectivo)
public class CursoResponseDTO {

    private Long idCurso;
    private String nombre;
    private int anioCarrera;
    private String comision;
    private Turno turno;
    // Relacion (ID plano)
    private Long idCarrera;
    private int anioLectivo;
    // Campos de auditoria
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public CursoResponseDTO() {}

    // Conversion desde entidad - extrae idCarrera como ID plano
    public static CursoResponseDTO fromEntity(Curso curso) {
        CursoResponseDTO dto = new CursoResponseDTO();
        dto.idCurso = curso.getIdCurso();
        dto.nombre = curso.getNombre();
        dto.anioCarrera = curso.getAnioCarrera();
        dto.comision = curso.getComision();
        dto.turno = curso.getTurno();
        if (curso.getCarrera() != null) {
            dto.idCarrera = curso.getCarrera().getIdCarrera();
        }
        dto.anioLectivo = curso.getAnioLectivo();
        dto.fechaCreacion = curso.getFechaCreacion();
        dto.activo = curso.isActivo();
        return dto;
    }

    // Getters
    public Long getIdCurso() { return idCurso; }
    public String getNombre() { return nombre; }
    public int getAnioCarrera() { return anioCarrera; }
    public String getComision() { return comision; }
    public Turno getTurno() { return turno; }
    public Long getIdCarrera() { return idCarrera; }
    public int getAnioLectivo() { return anioLectivo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
