package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrera")
public class Carrera {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private Long idCarrera;

    private String nombre;
    private String descripcion;

    @Column(name = "duracion_anios")
    private int duracionAnios;

    private String titulo;

    @ManyToOne
    @JoinColumn(name = "fk_id_institucion")
    private Institucion institucion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)
    private List<Curso> cursos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)
    private List<Materia> materias = new ArrayList<>();

    //Constructores
    public Carrera() {}

    public Carrera(String nombre, String descripcion, int duracionAnios, String titulo, Institucion institucion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionAnios = duracionAnios;
        this.titulo = titulo;
        this.institucion = institucion;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdCarrera() {
        return idCarrera;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDuracionAnios() {
        return duracionAnios;
    }
    public void setDuracionAnios(int duracionAnios) {
        this.duracionAnios = duracionAnios;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Institucion getInstitucion() {
        return institucion;
    }
    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
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

    public List<Curso> getCursos() {
        return cursos;
    }
    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }

    public List<Materia> getMaterias() {
        return materias;
    }
    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
}
