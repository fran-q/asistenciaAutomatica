package com.appasistencia.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AsistenciaDTO {

    @NotNull(message = "El ID del profesor es obligatorio")
    private Long idProfesor;

    private Long idAsignacion;

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "La hora de entrada es obligatoria")
    private String horaEntrada;

    private String horaSalida;

    @NotBlank(message = "El estado de asistencia es obligatorio")
    private String estado;

    @NotBlank(message = "El modo de registro es obligatorio")
    private String modoRegistro;

    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
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
