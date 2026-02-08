package com.appasistencia.dtos;

public class UsuarioAlumnoDTO {
    private Long idUsuario;
    private String legajo;
    private Double promedio;

    public UsuarioAlumnoDTO() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public Double getPromedio() { return promedio; }
    public void setPromedio(Double promedio) { this.promedio = promedio; }
}
