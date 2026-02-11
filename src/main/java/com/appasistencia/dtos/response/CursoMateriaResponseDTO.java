package com.appasistencia.dtos.response;

import com.appasistencia.models.CursoMateria;
import java.time.LocalDateTime;

public class CursoMateriaResponseDTO {

    private Long idCursoMateria;
    private Long idCurso;
    private Long idMateria;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public CursoMateriaResponseDTO() {}

    public static CursoMateriaResponseDTO fromEntity(CursoMateria cursoMateria) {
        CursoMateriaResponseDTO dto = new CursoMateriaResponseDTO();
        dto.idCursoMateria = cursoMateria.getIdCursoMateria();
        if (cursoMateria.getCurso() != null) {
            dto.idCurso = cursoMateria.getCurso().getIdCurso();
        }
        if (cursoMateria.getMateria() != null) {
            dto.idMateria = cursoMateria.getMateria().getIdMateria();
        }
        dto.fechaCreacion = cursoMateria.getFechaCreacion();
        dto.activo = cursoMateria.isActivo();
        return dto;
    }

    // Getters
    public Long getIdCursoMateria() { return idCursoMateria; }
    public Long getIdCurso() { return idCurso; }
    public Long getIdMateria() { return idMateria; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
