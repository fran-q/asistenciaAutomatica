package com.appasistencia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO de entrada: email para reenvio de codigo de verificacion
public class ReenvioCodigoDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    private String email;

    public ReenvioCodigoDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
