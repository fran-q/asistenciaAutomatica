package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad: Perfil de profesor vinculado a un Usuario
@Entity
@Table(name = "usuario_profesor", uniqueConstraints = @UniqueConstraint(columnNames = "legajo"))
public class UsuarioProfesor {

    // Identificador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profesor")
    private Long idProfesor;

    // Vinculo con el usuario base (relacion 1:1)
    @OneToOne
    @JoinColumn(name = "fk_id_usuario", unique = true)
    private Usuario usuario;

    // Datos academicos del profesor
    private String legajo;
    private String titulo;

    @Enumerated(EnumType.STRING)
    private CategoriaProfesor categoria;

    // Auditoria
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    // Relaciones: asignaciones a curso-materias y registros de asistencia
    @JsonIgnore
    @OneToMany(mappedBy = "profesor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Asignacion> asignaciones = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "profesor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Asistencia> asistencias = new ArrayList<>();

    //Constructores
    public UsuarioProfesor() {}

    public UsuarioProfesor(Usuario usuario, String legajo, String titulo, CategoriaProfesor categoria) {
        this.usuario = usuario;
        this.legajo = legajo;
        this.titulo = titulo;
        this.categoria = categoria;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdProfesor() {
        return idProfesor;
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

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public CategoriaProfesor getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaProfesor categoria) {
        this.categoria = categoria;
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

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }
    public void setAsignaciones(List<Asignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public List<Asistencia> getAsistencias() {
        return asistencias;
    }
    public void setAsistencias(List<Asistencia> asistencias) {
        this.asistencias = asistencias;
    }
}
