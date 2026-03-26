package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioDTO;
import com.appasistencia.dtos.response.UsuarioResponseDTO;
import com.appasistencia.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de usuarios
@RestController
@RequestMapping("/api/usuarios")
public class  UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/usuarios - listar usuarios filtrados por institucion del JWT
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(usuarioService.listarTodos(idInst));
    }

    // GET /api/usuarios/{id} - obtener usuario por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(usuarioService.obtenerPorId(id, idInst));
    }

    // GET /api/usuarios/rol/{rol} - listar usuarios por rol (ADMIN, PROFESOR, ALUMNO)
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(@PathVariable String rol, HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(usuarioService.listarPorRol(rol, idInst));
    }

    // POST /api/usuarios - crear usuario (asigna institucion del JWT)
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioDTO dto, HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        dto.setIdInstitucion(idInst);
        UsuarioResponseDTO creado = usuarioService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // PUT /api/usuarios/{id} - actualizar usuario validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        dto.setIdInstitucion(idInst);
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(usuarioService.actualizar(id, dto, idInst));
    }

    // DELETE /api/usuarios/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        usuarioService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/usuarios/{id}/reactivar - reactivar usuario validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        usuarioService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
