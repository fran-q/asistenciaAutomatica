package com.appasistencia.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "fk_id_alumno")
    private UsuarioAlumno alumno;

    @ManyToOne
    @JoinColumn(name = "fk_id_asistencia")
    private Asistencia asistencia;

    @ManyToOne
    @JoinColumn(name = "fk_id_asignacion")
    private Asignacion asignacion;

    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    //Constructores
    public Notificacion() {}

    public Notificacion(UsuarioAlumno alumno, Asistencia asistencia, Asignacion asignacion,
                        TipoNotificacion tipo, String titulo, String mensaje) {
        this.alumno = alumno;
        this.asistencia = asistencia;
        this.asignacion = asignacion;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public UsuarioAlumno getAlumno() {
        return alumno;
    }
    public void setAlumno(UsuarioAlumno alumno) {
        this.alumno = alumno;
    }

    public Asistencia getAsistencia() {
        return asistencia;
    }
    public void setAsistencia(Asistencia asistencia) {
        this.asistencia = asistencia;
    }

    public Asignacion getAsignacion() {
        return asignacion;
    }
    public void setAsignacion(Asignacion asignacion) {
        this.asignacion = asignacion;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }
    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
