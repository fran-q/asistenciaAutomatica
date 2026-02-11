package com.appasistencia.dtos;

import jakarta.validation.constraints.NotNull;

public class CursoMateriaDTO {

    @NotNull(message = "El ID del curso es obligatorio")
    private Long idCurso;

    @NotNull(message = "El ID de la materia es obligatorio")
    private Long idMateria;

    public CursoMateriaDTO() {}

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }

    public Long getIdMateria() { return idMateria; }
    public void setIdMateria(Long idMateria) { this.idMateria = idMateria; }
}
