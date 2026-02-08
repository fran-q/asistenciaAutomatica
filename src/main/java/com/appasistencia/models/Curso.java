package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curso")
public class Curso {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long idCurso;

    private String nombre;

    @Column(name = "anio_carrera")
    private int anioCarrera;

    private String comision;

    @Enumerated(EnumType.STRING)
    private Turno turno;

    @ManyToOne
    @JoinColumn(name = "fk_id_carrera")
    private Carrera carrera;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "anio_lectivo")
    private int anioLectivo;

    private boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<CursoMateria> cursoMaterias = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    //Constructores
    public Curso() {}

    public Curso(String nombre, int anioCarrera, String comision, Turno turno, Carrera carrera, int anioLectivo) {
        this.nombre = nombre;
        this.anioCarrera = anioCarrera;
        this.comision = comision;
        this.turno = turno;
        this.carrera = carrera;
        this.anioLectivo = anioLectivo;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    //Getters y Setters
    public Long getIdCurso() {
        return idCurso;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAnioCarrera() {
        return anioCarrera;
    }
    public void setAnioCarrera(int anioCarrera) {
        this.anioCarrera = anioCarrera;
    }

    public String getComision() {
        return comision;
    }
    public void setComision(String comision) {
        this.comision = comision;
    }

    public Turno getTurno() {
        return turno;
    }
    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Carrera getCarrera() {
        return carrera;
    }
    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getAnioLectivo() {
        return anioLectivo;
    }
    public void setAnioLectivo(int anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<CursoMateria> getCursoMaterias() {
        return cursoMaterias;
    }
    public void setCursoMaterias(List<CursoMateria> cursoMaterias) {
        this.cursoMaterias = cursoMaterias;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }
    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }
}
