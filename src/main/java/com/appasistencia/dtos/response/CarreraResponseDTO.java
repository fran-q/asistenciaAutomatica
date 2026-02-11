package com.appasistencia.dtos.response;

import com.appasistencia.models.Carrera;
import java.time.LocalDateTime;

public class CarreraResponseDTO {

    private Long idCarrera;
    private String nombre;
    private String descripcion;
    private int duracionAnios;
    private String titulo;
    private Long idInstitucion;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    public CarreraResponseDTO() {}

    public static CarreraResponseDTO fromEntity(Carrera carrera) {
        CarreraResponseDTO dto = new CarreraResponseDTO();
        dto.idCarrera = carrera.getIdCarrera();
        dto.nombre = carrera.getNombre();
        dto.descripcion = carrera.getDescripcion();
        dto.duracionAnios = carrera.getDuracionAnios();
        dto.titulo = carrera.getTitulo();
        if (carrera.getInstitucion() != null) {
            dto.idInstitucion = carrera.getInstitucion().getIdInstitucion();
        }
        dto.fechaCreacion = carrera.getFechaCreacion();
        dto.activo = carrera.isActivo();
        return dto;
    }

    // Getters
    public Long getIdCarrera() { return idCarrera; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getDuracionAnios() { return duracionAnios; }
    public String getTitulo() { return titulo; }
    public Long getIdInstitucion() { return idInstitucion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
}
