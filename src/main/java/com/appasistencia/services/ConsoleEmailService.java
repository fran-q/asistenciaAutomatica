package com.appasistencia.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

// Implementacion de desarrollo: imprime el codigo en la consola del servidor
// Se activa automaticamente cuando NO hay SMTP configurado
@Service
@ConditionalOnMissingBean(SmtpEmailService.class)
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void enviarCodigoVerificacion(String destinatario, String codigo, String nombreUsuario) {
        log.warn("========================================");
        log.warn("  CODIGO DE VERIFICACION (modo dev)");
        log.warn("  Para: {}", destinatario);
        log.warn("  Usuario: {}", nombreUsuario);
        log.warn("  Codigo: {}", codigo);
        log.warn("========================================");
    }
}
