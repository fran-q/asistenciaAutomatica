package com.appasistencia.services;

import com.appasistencia.dtos.AuthResponseDTO;
import com.appasistencia.dtos.MensajeDTO;
import com.appasistencia.dtos.UsuarioDTO;
import com.appasistencia.dtos.response.UsuarioResponseDTO;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.Rol;
import com.appasistencia.models.Usuario;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Servicio: autenticacion (login y registro de usuarios)
@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final VerificacionService verificacionService;

    public AuthService(UsuarioRepository usuarioRepository, UsuarioService usuarioService,
                       JwtService jwtService, PasswordEncoder passwordEncoder,
                       VerificacionService verificacionService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.verificacionService = verificacionService;
    }

    // Validar credenciales y generar token JWT
    public AuthResponseDTO login(String email, String contrasena) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new OperacionInvalidaException("Este email no tiene una cuenta creada"));

        if (!usuario.isActivo()) {
            throw new OperacionInvalidaException("Este email no tiene una cuenta creada");
        }

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new OperacionInvalidaException("Contrasena incorrecta");
        }

        // Bloquear login si el admin no verifico su email
        if (!usuario.isVerificado() && usuario.getRol() == Rol.ADMIN) {
            throw new OperacionInvalidaException("Debes verificar tu email antes de iniciar sesion");
        }

        String token = jwtService.generateToken(
                usuario.getIdUsuario(), usuario.getEmail(),
                usuario.getRol().name(), usuario.getInstitucion().getIdInstitucion(),
                usuario.getNombre(), usuario.getApellido()
        );

        return new AuthResponseDTO(token, UsuarioResponseDTO.fromEntity(usuario));
    }

    // Registrar nuevo admin: crea usuario sin verificar y envia codigo por email
    public MensajeDTO register(UsuarioDTO dto) {
        dto.setRol("ADMIN");

        UsuarioResponseDTO created = usuarioService.crear(dto);
        Usuario usuario = usuarioService.buscarPorId(created.getIdUsuario());

        // Marcar como no verificado (el default es true para usuarios creados via CRUD)
        usuario.setVerificado(false);
        usuarioRepository.save(usuario);

        // Enviar codigo de verificacion por email
        verificacionService.crearYEnviarCodigo(usuario);

        return new MensajeDTO("Se envio un codigo de verificacion a " + usuario.getEmail());
    }
}
