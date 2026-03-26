package com.appasistencia.dtos;

import com.appasistencia.dtos.response.UsuarioResponseDTO;

// DTO de respuesta: resultado de autenticacion con token JWT y datos del usuario
public class AuthResponseDTO {

    private String token;
    private UsuarioResponseDTO usuario;

    public AuthResponseDTO(String token, UsuarioResponseDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() { return token; }
    public UsuarioResponseDTO getUsuario() { return usuario; }
}
