package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioDTO;
import com.appasistencia.models.Usuario;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    // Listado de todos los usuarios ACTIVOS
    @GetMapping("/usuarios")
    public List<UsuarioDTO> getUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                // solo usuarios con esActivo = true
                .filter(u -> Boolean.TRUE.equals(u.getEsActivo()))
                .map(UsuarioDTO::new)
                .toList();
    }


    //Busqueda de usuario por ID
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> getUsuario(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.ok(new UsuarioDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    //Obtener usuario actual en sesion
    @GetMapping("/usuarios/current")
    public ResponseEntity<UsuarioDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String correo = authentication.getName(); // el username es el correo
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(new UsuarioDTO(usuario), HttpStatus.OK);
    }

    //Crea nuevo usuario, si el correo ya existe devuelve un CONFLICT
    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        if (usuario.getCorreo() != null && usuarioRepository.findByCorreo(usuario.getCorreo()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo ya está registrado");
        }
        //Encriptamos la contraseña antes de guardar
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        //Guardamos el usuario
        usuario.setEsActivo(true);
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario creado correctamente");
    }

    //Atualizacion del usuario
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario nuevosDatos) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(nuevosDatos.getNombre());
                    usuario.setApellido(nuevosDatos.getApellido());
                    usuario.setCorreo(nuevosDatos.getCorreo());
                    usuario.setTelefono(nuevosDatos.getTelefono());
                    usuario.setRol(nuevosDatos.getRol());
                    // si viene contraseña nueva, la ciframos
                    if (nuevosDatos.getContrasena() != null &&
                            !nuevosDatos.getContrasena().isBlank()) {
                        usuario.setContrasena(
                                passwordEncoder.encode(nuevosDatos.getContrasena()));
                    }
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok("Usuario actualizado correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //Baja logica del usuario
    @PatchMapping("/usuarios/{id}/baja")
    public ResponseEntity<?> darBajaUsuario(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setEsActivo(false);
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok("Usuario dado de baja correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}