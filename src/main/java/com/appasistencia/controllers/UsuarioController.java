package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioDTO;
import com.appasistencia.models.*;
import com.appasistencia.repositories.InstitucionRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final InstitucionRepository institucionRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, InstitucionRepository institucionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.institucionRepository = institucionRepository;
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{rol}")
    public List<Usuario> listarPorRol(@PathVariable String rol) {
        return usuarioRepository.findByRolAndActivoTrue(Rol.valueOf(rol.toUpperCase()));
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody UsuarioDTO dto) {
        Usuario usuario = new Usuario(
                dto.getNombre(), dto.getApellido(), dto.getEmail(), dto.getTelefono(),
                dto.getDireccion(), TipoDocumento.valueOf(dto.getTipoDocumento()),
                dto.getNumeroDocumento(), Genero.valueOf(dto.getGenero()),
                dto.getContrasena(), Rol.valueOf(dto.getRol())
        );
        usuario.setFotoPerfil(dto.getFotoPerfil());

        if (dto.getIdInstitucion() != null) {
            institucionRepository.findById(dto.getIdInstitucion())
                    .ifPresent(usuario::setInstitucion);
        }

        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setEmail(dto.getEmail());
            usuario.setTelefono(dto.getTelefono());
            usuario.setDireccion(dto.getDireccion());
            if (dto.getTipoDocumento() != null) usuario.setTipoDocumento(TipoDocumento.valueOf(dto.getTipoDocumento()));
            usuario.setNumeroDocumento(dto.getNumeroDocumento());
            if (dto.getGenero() != null) usuario.setGenero(Genero.valueOf(dto.getGenero()));
            if (dto.getContrasena() != null) usuario.setContrasena(dto.getContrasena());
            if (dto.getRol() != null) usuario.setRol(Rol.valueOf(dto.getRol()));
            usuario.setFotoPerfil(dto.getFotoPerfil());

            if (dto.getIdInstitucion() != null) {
                institucionRepository.findById(dto.getIdInstitucion())
                        .ifPresent(usuario::setInstitucion);
            }

            return ResponseEntity.ok(usuarioRepository.save(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
