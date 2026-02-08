package com.appasistencia.dtos;

public class CarreraDTO {
    private String nombre;
    private String descripcion;
    private int duracionAnios;
    private String titulo;
    private Long idInstitucion;

    public CarreraDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getDuracionAnios() { return duracionAnios; }
    public void setDuracionAnios(int duracionAnios) { this.duracionAnios = duracionAnios; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Long getIdInstitucion() { return idInstitucion; }
    public void setIdInstitucion(Long idInstitucion) { this.idInstitucion = idInstitucion; }
}
