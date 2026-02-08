package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "asistencia")
public class Asistencia {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAsistencia;

    @ManyToOne
    @JoinColumn(name = "fk_id_profesor")
    private UsuarioProfesor profesor;

    @ManyToOne
    @JoinColumn(name = "fk_id_asignacion")
    private Asignacion asignacion;

    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Enumerated(EnumType.STRING)
    private EstadoAsistencia estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_registro")
    private ModoRegistro modoRegistro;

    private String observaciones;

    private boolean activo = true;

    //Constructores
    public Asistencia() {}

    public Asistencia(UsuarioProfesor profesor, Asignacion asignacion, LocalDate fecha,
                      LocalTime horaEntrada, EstadoAsistencia estado, ModoRegistro modoRegistro) {
        this.profesor = profesor;
        this.asignacion = asignacion;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.estado = estado;
        this.modoRegistro = modoRegistro;
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdAsistencia() {
        return idAsistencia;
    }

    public UsuarioProfesor getProfesor() {
        return profesor;
    }
    public void setProfesor(UsuarioProfesor profesor) {
        this.profesor = profesor;
    }

    public Asignacion getAsignacion() {
        return asignacion;
    }
    public void setAsignacion(Asignacion asignacion) {
        this.asignacion = asignacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }
    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }
    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }
    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public ModoRegistro getModoRegistro() {
        return modoRegistro;
    }
    public void setModoRegistro(ModoRegistro modoRegistro) {
        this.modoRegistro = modoRegistro;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
