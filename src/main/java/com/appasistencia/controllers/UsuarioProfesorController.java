package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioProfesorDTO;
import com.appasistencia.dtos.response.UsuarioProfesorResponseDTO;
import com.appasistencia.services.UsuarioProfesorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de perfiles de profesores
@RestController
@RequestMapping("/api/profesores")
public class UsuarioProfesorController {

    private final UsuarioProfesorService profesorService;

    public UsuarioProfesorController(UsuarioProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    // GET /api/profesores - listar perfiles profesor filtrados por institucion
    @GetMapping
    public ResponseEntity<List<UsuarioProfesorResponseDTO>> listarTodos(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(profesorService.listarTodos(idInst));
    }

    // GET /api/profesores/{id} - obtener perfil profesor por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioProfesorResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(profesorService.obtenerPorId(id, idInst));
    }

    // POST /api/profesores - crear perfil profesor vinculado a usuario
    @PostMapping
    public ResponseEntity<UsuarioProfesorResponseDTO> crear(@Valid @RequestBody UsuarioProfesorDTO dto) {
        UsuarioProfesorResponseDTO creado = profesorService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // PUT /api/profesores/{id} - actualizar perfil profesor validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioProfesorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioProfesorDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(profesorService.actualizar(id, dto, idInst));
    }

    // DELETE /api/profesores/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        profesorService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/profesores/{id}/reactivar - reactivar perfil profesor validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        profesorService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
