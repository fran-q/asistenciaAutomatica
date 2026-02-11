package com.appasistencia.dtos.response;

import com.appasistencia.models.Curso;
import com.appasistencia.models.Turno;
import java.time.LocalDateTime;

public class CursoResponseDTO {

    private Long idCurso;
    private String nombre;
    private int anioCarrera;
    private String comision;
    private Turno turno;
    private Long idCarrera;
    private int anioLectivo;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public CursoResponseDTO() {}

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
