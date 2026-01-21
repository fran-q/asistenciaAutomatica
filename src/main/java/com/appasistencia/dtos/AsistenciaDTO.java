package com.appasistencia.dtos;

import com.appasistencia.models.Asistencia;
import java.time.LocalDateTime;

public class AsistenciaDTO {
    //Atributos
    private int id;
    private LocalDateTime horaIngresoRegistro;
    private LocalDateTime horaSalidaRegistro;
    private Boolean verificadoBiometrico;
    private String observaciones;

    //Constructores
    public AsistenciaDTO() {}

    public AsistenciaDTO(Asistencia asistencia) {
        this.id = asistencia.getId();
        this.horaIngresoRegistro = asistencia.getHoraIngresoRegistro();
        this.horaSalidaRegistro = asistencia.getHoraSalidaRegistro();
        this.verificadoBiometrico = asistencia.getVerificadoBiometrico();
        this.observaciones = asistencia.getObservaciones();
    }

    //Getters
    public int getId() {
        return id;
    }

    public LocalDateTime getHoraIngresoRegistro() {
        return horaIngresoRegistro;
    }

    public LocalDateTime getHoraSalidaRegistro() {
        return horaSalidaRegistro;
    }

    public Boolean getVerificadoBiometrico() {
        return verificadoBiometrico;
    }

    public String getObservaciones() {
        return observaciones;
    }

}