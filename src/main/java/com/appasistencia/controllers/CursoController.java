package com.appasistencia.controllers;

import com.appasistencia.dtos.CursoDTO;
import com.appasistencia.dtos.response.CursoResponseDTO;
import com.appasistencia.services.CursoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de cursos
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // GET /api/cursos - listar cursos filtrados por institucion del JWT
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarTodos(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(cursoService.listarTodos(idInst));
    }

    // GET /api/cursos/{id} - obtener curso por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(cursoService.obtenerPorId(id, idInst));
    }

    // GET /api/cursos/carrera/{idCarrera} - listar cursos de una carrera
    @GetMapping("/carrera/{idCarrera}")
    public ResponseEntity<List<CursoResponseDTO>> listarPorCarrera(@PathVariable Long idCarrera) {
        return ResponseEntity.ok(cursoService.listarPorCarrera(idCarrera));
    }

    // POST /api/cursos - crear nuevo curso
    @PostMapping
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoDTO dto) {
        CursoResponseDTO creado = cursoService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // PUT /api/cursos/{id} - actualizar curso validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CursoDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(cursoService.actualizar(id, dto, idInst));
    }

    // DELETE /api/cursos/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        cursoService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/cursos/{id}/reactivar - reactivar curso validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        cursoService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
