package com.appasistencia.dtos.response;

import com.appasistencia.models.Asistencia;
import com.appasistencia.models.EstadoAsistencia;
import com.appasistencia.models.ModoRegistro;
import java.time.LocalDate;
import java.time.LocalTime;

public class AsistenciaResponseDTO {

    private Long idAsistencia;
    private Long idProfesor;
    private Long idAsignacion;
    private LocalDate fecha;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private EstadoAsistencia estado;
    private ModoRegistro modoRegistro;
    private String observaciones;
    private boolean activo;

    public AsistenciaResponseDTO() {}

    public static AsistenciaResponseDTO fromEntity(Asistencia asistencia) {
        AsistenciaResponseDTO dto = new AsistenciaResponseDTO();
        dto.idAsistencia = asistencia.getIdAsistencia();
        if (asistencia.getProfesor() != null) {
            dto.idProfesor = asistencia.getProfesor().getIdProfesor();
        }
        if (asistencia.getAsignacion() != null) {
            dto.idAsignacion = asistencia.getAsignacion().getIdAsignacion();
        }
        dto.fecha = asistencia.getFecha();
        dto.horaEntrada = asistencia.getHoraEntrada();
        dto.horaSalida = asistencia.getHoraSalida();
        dto.estado = asistencia.getEstado();
        dto.modoRegistro = asistencia.getModoRegistro();
        dto.observaciones = asistencia.getObservaciones();
        dto.activo = asistencia.isActivo();
        return dto;
    }

    // Getters
    public Long getIdAsistencia() { return idAsistencia; }
    public Long getIdProfesor() { return idProfesor; }
    public Long getIdAsignacion() { return idAsignacion; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraEntrada() { return horaEntrada; }
    public LocalTime getHoraSalida() { return horaSalida; }
    public EstadoAsistencia getEstado() { return estado; }
    public ModoRegistro getModoRegistro() { return modoRegistro; }
    public String getObservaciones() { return observaciones; }
    public boolean isActivo() { return activo; }
}
