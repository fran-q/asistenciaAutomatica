package com.appasistencia.controllers;

import com.appasistencia.dtos.*;
import com.appasistencia.dtos.response.InstitucionResponseDTO;
import com.appasistencia.dtos.response.UsuarioResponseDTO;
import com.appasistencia.models.Usuario;
import com.appasistencia.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de autenticacion (login, registro, verificacion, sesion)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final InstitucionService institucionService;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final VerificacionService verificacionService;

    public AuthController(AuthService authService, InstitucionService institucionService,
                          JwtService jwtService, UsuarioService usuarioService,
                          VerificacionService verificacionService) {
        this.authService = authService;
        this.institucionService = institucionService;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.verificacionService = verificacionService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto.getEmail(), dto.getContrasena()));
    }

    // Registro de admin: crea usuario sin verificar, envia codigo por email
    @PostMapping("/register")
    public ResponseEntity<MensajeDTO> register(@Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    // Verificar codigo de email y obtener token JWT
    @PostMapping("/verificar")
    public ResponseEntity<AuthResponseDTO> verificar(@Valid @RequestBody VerificacionDTO dto) {
        Usuario usuario = verificacionService.verificarCodigo(dto.getEmail(), dto.getCodigo());

        String token = jwtService.generateToken(
                usuario.getIdUsuario(), usuario.getEmail(),
                usuario.getRol().name(), usuario.getInstitucion().getIdInstitucion(),
                usuario.getNombre(), usuario.getApellido()
        );

        return ResponseEntity.ok(new AuthResponseDTO(token, UsuarioResponseDTO.fromEntity(usuario)));
    }

    // Reenviar codigo de verificacion (rate-limited a 60s)
    @PostMapping("/reenviar-codigo")
    public ResponseEntity<MensajeDTO> reenviarCodigo(@Valid @RequestBody ReenvioCodigoDTO dto) {
        verificacionService.reenviarCodigo(dto.getEmail());
        return ResponseEntity.ok(new MensajeDTO("Codigo reenviado exitosamente"));
    }

    @PostMapping("/register-institucion")
    public ResponseEntity<InstitucionResponseDTO> registerInstitucion(@Valid @RequestBody InstitucionDTO dto) {
        return ResponseEntity.ok(institucionService.crear(dto));
    }

    @GetMapping("/instituciones")
    public ResponseEntity<List<InstitucionResponseDTO>> listarInstituciones() {
        return ResponseEntity.ok(institucionService.listarTodas());
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(HttpServletRequest request) {
        Long idUsuario = (Long) request.getAttribute("idUsuario");
        return ResponseEntity.ok(usuarioService.obtenerPorId(idUsuario));
    }
}
