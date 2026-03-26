package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioAlumnoDTO;
import com.appasistencia.dtos.response.UsuarioAlumnoResponseDTO;
import com.appasistencia.services.UsuarioAlumnoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de perfiles de alumnos
@RestController
@RequestMapping("/api/alumnos")
public class UsuarioAlumnoController {

    private final UsuarioAlumnoService alumnoService;

    public UsuarioAlumnoController(UsuarioAlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    // GET /api/alumnos - listar perfiles alumno filtrados por institucion
    @GetMapping
    public ResponseEntity<List<UsuarioAlumnoResponseDTO>> listarTodos(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(alumnoService.listarTodos(idInst));
    }

    // GET /api/alumnos/{id} - obtener perfil alumno por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioAlumnoResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(alumnoService.obtenerPorId(id, idInst));
    }

    // POST /api/alumnos - crear perfil alumno vinculado a usuario
    @PostMapping
    public ResponseEntity<UsuarioAlumnoResponseDTO> crear(@Valid @RequestBody UsuarioAlumnoDTO dto) {
        UsuarioAlumnoResponseDTO creado = alumnoService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // PUT /api/alumnos/{id} - actualizar perfil alumno validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioAlumnoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioAlumnoDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(alumnoService.actualizar(id, dto, idInst));
    }

    // DELETE /api/alumnos/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        alumnoService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/alumnos/{id}/reactivar - reactivar perfil alumno validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        alumnoService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
