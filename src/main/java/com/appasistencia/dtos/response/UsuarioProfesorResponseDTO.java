package com.appasistencia.dtos.response;

import com.appasistencia.models.CategoriaProfesor;
import com.appasistencia.models.UsuarioProfesor;
import java.time.LocalDateTime;

public class UsuarioProfesorResponseDTO {

    private Long idProfesor;
    private Long idUsuario;
    private String legajo;
    private String titulo;
    private CategoriaProfesor categoria;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public UsuarioProfesorResponseDTO() {}

    public static UsuarioProfesorResponseDTO fromEntity(UsuarioProfesor profesor) {
        UsuarioProfesorResponseDTO dto = new UsuarioProfesorResponseDTO();
        dto.idProfesor = profesor.getIdProfesor();
        if (profesor.getUsuario() != null) {
            dto.idUsuario = profesor.getUsuario().getIdUsuario();
        }
        dto.legajo = profesor.getLegajo();
        dto.titulo = profesor.getTitulo();
        dto.categoria = profesor.getCategoria();
        dto.fechaCreacion = profesor.getFechaCreacion();
        dto.activo = profesor.isActivo();
        return dto;
    }

    // Getters
    public Long getIdProfesor() { return idProfesor; }
    public Long getIdUsuario() { return idUsuario; }
    public String getLegajo() { return legajo; }
    public String getTitulo() { return titulo; }
    public CategoriaProfesor getCategoria() { return categoria; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
