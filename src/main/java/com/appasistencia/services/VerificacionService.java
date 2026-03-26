package com.appasistencia.services;

import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.CodigoVerificacion;
import com.appasistencia.models.Usuario;
import com.appasistencia.repositories.CodigoVerificacionRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

// Servicio: logica de generacion, envio y validacion de codigos de verificacion
@Service
@Transactional
public class VerificacionService {

    private static final int CODIGO_LENGTH = 5;
    private static final int EXPIRACION_MINUTOS = 2;
    private static final int COOLDOWN_SEGUNDOS = 60;
    private static final int MAX_INTENTOS = 10;
    // Sin 0/O/1/I/l para evitar confusion visual
    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final CodigoVerificacionRepository codigoRepo;
    private final UsuarioRepository usuarioRepo;
    private final EmailService emailService;

    public VerificacionService(CodigoVerificacionRepository codigoRepo,
                               UsuarioRepository usuarioRepo,
                               EmailService emailService) {
        this.codigoRepo = codigoRepo;
        this.usuarioRepo = usuarioRepo;
        this.emailService = emailService;
    }

    // Genera codigo alfanumerico de 5 caracteres con SecureRandom
    public String generarCodigo() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODIGO_LENGTH);
        for (int i = 0; i < CODIGO_LENGTH; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }

    // Crea codigo, lo persiste y lo envia por email
    public void crearYEnviarCodigo(Usuario usuario) {
        // Eliminar codigo previo si existe
        codigoRepo.findByUsuario(usuario).ifPresent(codigoRepo::delete);
        codigoRepo.flush();

        String codigo = generarCodigo();
        CodigoVerificacion cv = new CodigoVerificacion(
                codigo,
                LocalDateTime.now().plusMinutes(EXPIRACION_MINUTOS),
                usuario
        );
        codigoRepo.save(cv);

        emailService.enviarCodigoVerificacion(usuario.getEmail(), codigo, usuario.getNombre());
    }

    // Valida el codigo ingresado y marca al usuario como verificado
    // Si el codigo expiro o se superan los 10 intentos, elimina el usuario para que rehaga el registro
    public Usuario verificarCodigo(String email, String codigo) {
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new OperacionInvalidaException("Email no encontrado"));

        if (usuario.isVerificado()) {
            throw new OperacionInvalidaException("El usuario ya esta verificado");
        }

        CodigoVerificacion cv = codigoRepo.findByUsuario(usuario)
                .orElseThrow(() -> new OperacionInvalidaException("No hay codigo de verificacion pendiente. Debe registrarse nuevamente."));

        // Verificar expiracion: si expiro, eliminar usuario y codigo
        if (cv.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            codigoRepo.delete(cv);
            codigoRepo.flush();
            usuarioRepo.delete(usuario);
            throw new OperacionInvalidaException("El tiempo de verificacion ha expirado. Debe registrarse nuevamente.");
        }

        // Verificar maximo de intentos: si supero, eliminar usuario y codigo
        if (cv.getIntentos() >= MAX_INTENTOS) {
            codigoRepo.delete(cv);
            codigoRepo.flush();
            usuarioRepo.delete(usuario);
            throw new OperacionInvalidaException("Se ha superado el limite de intentos. Debe registrarse nuevamente.");
        }

        // Comparar codigo (case-insensitive)
        if (!cv.getCodigo().equalsIgnoreCase(codigo.trim())) {
            cv.incrementarIntentos();
            codigoRepo.save(cv);
            throw new OperacionInvalidaException("Codigo de verificacion incorrecto");
        }

        // Verificar usuario y limpiar codigo
        usuario.setVerificado(true);
        usuarioRepo.save(usuario);
        codigoRepo.delete(cv);

        return usuario;
    }

    // Reenvia codigo con rate-limit de 60 segundos
    public void reenviarCodigo(String email) {
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new OperacionInvalidaException("Email no encontrado"));

        if (usuario.isVerificado()) {
            throw new OperacionInvalidaException("El usuario ya esta verificado");
        }

        // Rate-limit: verificar que el codigo anterior tenga al menos 60s de antigüedad
        Optional<CodigoVerificacion> existente = codigoRepo.findByUsuario(usuario);
        if (existente.isPresent()) {
            LocalDateTime creacion = existente.get().getFechaExpiracion().minusMinutes(EXPIRACION_MINUTOS);
            if (creacion.plusSeconds(COOLDOWN_SEGUNDOS).isAfter(LocalDateTime.now())) {
                throw new OperacionInvalidaException("Espera al menos 60 segundos antes de solicitar otro codigo");
            }
        }

        crearYEnviarCodigo(usuario);
    }
}
