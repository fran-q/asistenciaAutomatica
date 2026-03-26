package com.appasistencia.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad: Usuario base del sistema (puede tener perfil de Profesor o Alumno)
@Entity
@Table(name = "usuario")
public class Usuario {

    // Identificador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    // Datos personales
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    // Credenciales y rol
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    // Relacion con la institucion a la que pertenece
    @ManyToOne
    @JoinColumn(name = "fk_id_institucion")
    private Institucion institucion;

    // Auditoria
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private boolean activo = true;

    // Verificacion de email (true por defecto; solo auto-registro de admins lo pone en false)
    @Column(columnDefinition = "boolean default true")
    private boolean verificado = true;

    // Perfiles opcionales: profesor y/o alumno vinculados a este usuario
    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UsuarioProfesor usuarioProfesor;

    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UsuarioAlumno usuarioAlumno;

    // Plantillas biometricas para reconocimiento facial
    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantillaBiometrica> plantillasBiometricas = new ArrayList<>();

    //Constructores
    public Usuario() {}

    public Usuario(String nombre, String apellido, String email, String telefono, String direccion,
                   TipoDocumento tipoDocumento, String numeroDocumento, Genero genero,
                   String contrasena, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.genero = genero;
        this.contrasena = contrasena;
        this.rol = rol;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
        this.verificado = true;
    }

    //Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Genero getGenero() {
        return genero;
    }
    public void setGenero(Genero genero) {
        this.genero = genero;
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

    public String getFotoPerfil() {
        return fotoPerfil;
    }
    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
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

    public boolean isVerificado() {
        return verificado;
    }
    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public UsuarioProfesor getUsuarioProfesor() {
        return usuarioProfesor;
    }
    public void setUsuarioProfesor(UsuarioProfesor usuarioProfesor) {
        this.usuarioProfesor = usuarioProfesor;
    }

    public UsuarioAlumno getUsuarioAlumno() {
        return usuarioAlumno;
    }
    public void setUsuarioAlumno(UsuarioAlumno usuarioAlumno) {
        this.usuarioAlumno = usuarioAlumno;
    }

    public List<PlantillaBiometrica> getPlantillasBiometricas() {
        return plantillasBiometricas;
    }
    public void setPlantillasBiometricas(List<PlantillaBiometrica> plantillasBiometricas) {
        this.plantillasBiometricas = plantillasBiometricas;
    }

    public void addPlantillaBiometrica(PlantillaBiometrica plantilla) {
        plantilla.setUsuario(this);
        this.plantillasBiometricas.add(plantilla);
    }
}
