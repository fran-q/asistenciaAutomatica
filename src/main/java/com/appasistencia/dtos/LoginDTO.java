package com.appasistencia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO de entrada: credenciales de inicio de sesion
public class LoginDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
