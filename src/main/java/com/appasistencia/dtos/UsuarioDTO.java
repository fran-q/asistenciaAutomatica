package com.appasistencia.dtos;

import com.appasistencia.models.Usuario;

public class UsuarioDTO {
    //Atributos
    private int id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String rol;
    private boolean esActivo;

    //Constructores
    public UsuarioDTO() {}

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.correo = usuario.getCorreo();
        this.telefono = usuario.getTelefono();
        this.rol = usuario.getRol() != null ? usuario.getRol().name() : null;
        this.esActivo = usuario.getEsActivo();
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRol() {
        return rol;
    }

    public boolean isEsActivo() {
        return esActivo;
    }
}