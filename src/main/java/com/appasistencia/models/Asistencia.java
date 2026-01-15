package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
public class Asistencia {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime horaIngresoRegistro;
    private LocalDateTime horaSalidaRegistro;
    private Boolean verificadoBiometrico;
    private String observaciones;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    //Constructores
    public Asistencia() {}

    public Asistencia(LocalDateTime horaIngresoRegistro, LocalDateTime horaSalidaRegistro, Boolean verificadoBiometrico, String observaciones) {
        this.horaIngresoRegistro = horaIngresoRegistro;
        this.horaSalidaRegistro = horaSalidaRegistro;
        this.verificadoBiometrico = verificadoBiometrico;
        this.observaciones = observaciones;
    }

    //Getters y Setters
    public int getId() {
        return id;
    }

    public LocalDateTime getHoraIngresoRegistro() {
        return horaIngresoRegistro;
    }
    public void setHoraIngresoRegistro(LocalDateTime horaIngresoRegistro) {
        this.horaIngresoRegistro = horaIngresoRegistro;
    }

    public LocalDateTime getHoraSalidaRegistro() {
        return horaSalidaRegistro;
    }
    public void setHoraSalidaRegistro(LocalDateTime horaSalidaRegistro) {
        this.horaSalidaRegistro = horaSalidaRegistro;
    }

    public Boolean getVerificadoBiometrico() {
        return verificadoBiometrico;
    }
    public void setVerificadoBiometrico(Boolean verificadoBiometrico) {
        this.verificadoBiometrico = verificadoBiometrico;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}