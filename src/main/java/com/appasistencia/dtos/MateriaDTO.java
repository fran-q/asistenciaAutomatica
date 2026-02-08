package com.appasistencia.dtos;

public class MateriaDTO {
    private String nombre;
    private String descripcion;
    private int horasSemanales;
    private Long idCarrera;

    public MateriaDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getHorasSemanales() { return horasSemanales; }
    public void setHorasSemanales(int horasSemanales) { this.horasSemanales = horasSemanales; }

    public Long getIdCarrera() { return idCarrera; }
    public void setIdCarrera(Long idCarrera) { this.idCarrera = idCarrera; }
}
