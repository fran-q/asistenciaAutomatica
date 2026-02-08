package com.appasistencia.dtos;

public class CursoDTO {
    private String nombre;
    private int anioCarrera;
    private String comision;
    private String turno;
    private Long idCarrera;
    private int anioLectivo;

    public CursoDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getAnioCarrera() { return anioCarrera; }
    public void setAnioCarrera(int anioCarrera) { this.anioCarrera = anioCarrera; }

    public String getComision() { return comision; }
    public void setComision(String comision) { this.comision = comision; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Long getIdCarrera() { return idCarrera; }
    public void setIdCarrera(Long idCarrera) { this.idCarrera = idCarrera; }

    public int getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(int anioLectivo) { this.anioLectivo = anioLectivo; }
}
