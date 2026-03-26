package com.appasistencia.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// Implementacion SMTP: envia emails reales (activa solo si spring.mail.host esta configurado)
@Service
@ConditionalOnProperty(name = "spring.mail.host")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarCodigoVerificacion(String destinatario, String codigo, String nombreUsuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setFrom(fromEmail);
        message.setSubject("Codigo de verificacion - Sistema de Asistencia");
        message.setText(
            "Hola " + nombreUsuario + ",\n\n" +
            "Tu codigo de verificacion es: " + codigo + "\n\n" +
            "Este codigo expira en 15 minutos.\n\n" +
            "Si no solicitaste este codigo, ignora este mensaje."
        );
        mailSender.send(message);
    }
}
