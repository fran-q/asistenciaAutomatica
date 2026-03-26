package com.appasistencia.services;

// Interfaz: envio de emails de verificacion (implementada por SMTP o consola)
public interface EmailService {
    void enviarCodigoVerificacion(String destinatario, String codigo, String nombreUsuario);
}
