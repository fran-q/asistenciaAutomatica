package com.appasistencia.models;

import com.appasistencia.models.Rol;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Usuario {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String contrasena;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private boolean esActivo = true;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asistencia> asistencias = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioPlantillaBiometrica> usuarioPlantillas = new ArrayList<>();

    //Constructores
    public Usuario() {}

    public Usuario(String nombre, String apellido, String correo, String telefono, String contrasena, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.rol = rol;
        this.esActivo = true;
    }

    //Getters y Setters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean getEsActivo() {
        return esActivo;
    }
    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public void addAsistencia(Asistencia asistencia) {
        asistencia.setUsuario(this);
        this.asistencias.add(asistencia);
    }

    public void addHorario(Horario horario) {
        horario.setUsuario(this);
        this.horarios.add(horario);
    }

    public void addUsuarioPlantillaBiometrica(com.appasistencia.models.UsuarioPlantillaBiometrica usuarioPlantilla) {
        usuarioPlantilla.setUsuario(this);
        this.usuarioPlantillas.add(usuarioPlantilla);
        
    }

    public String getIdUsuario() {
        return null;
    }
}