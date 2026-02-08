package com.appasistencia.dtos;

public class AsistenciaDTO {
    private Long idProfesor;
    private Long idAsignacion;
    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String estado;
    private String modoRegistro;
    private String observaciones;

    public AsistenciaDTO() {}

    public Long getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Long idProfesor) { this.idProfesor = idProfesor; }

    public Long getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(Long idAsignacion) { this.idAsignacion = idAsignacion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }

    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getModoRegistro() { return modoRegistro; }
    public void setModoRegistro(String modoRegistro) { this.modoRegistro = modoRegistro; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
