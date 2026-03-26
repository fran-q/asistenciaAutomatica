package com.appasistencia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO de entrada: email + codigo de verificacion de 5 caracteres
public class VerificacionDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    private String email;

    @NotBlank(message = "El codigo es obligatorio")
    @Size(min = 5, max = 5, message = "El codigo debe tener 5 caracteres")
    private String codigo;

    public VerificacionDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
