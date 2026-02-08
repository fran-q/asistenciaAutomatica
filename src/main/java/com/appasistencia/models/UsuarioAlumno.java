package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario_alumno")
public class UsuarioAlumno {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Long idAlumno;

    @OneToOne
    @JoinColumn(name = "fk_id_usuario", unique = true)
    private Usuario usuario;

    private String legajo;
    private Double promedio;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Notificacion> notificaciones = new ArrayList<>();

    //Constructores
    public UsuarioAlumno() {}

    public UsuarioAlumno(Usuario usuario, String legajo, Double promedio) {
        this.usuario = usuario;
        this.legajo = legajo;
        this.promedio = promedio;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdAlumno() {
        return idAlumno;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getLegajo() {
        return legajo;
    }
    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public Double getPromedio() {
        return promedio;
    }
    public void setPromedio(Double promedio) {
        this.promedio = promedio;
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

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }
    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }
    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }
}
