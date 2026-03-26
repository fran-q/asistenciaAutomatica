package com.appasistencia.controllers;

import com.appasistencia.dtos.InscripcionDTO;
import com.appasistencia.dtos.response.InscripcionResponseDTO;
import com.appasistencia.services.InscripcionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de inscripciones alumno-curso
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> listarTodas(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(inscripcionService.listarTodas(idInst));
    }

    // GET /api/inscripciones/{id} - obtener inscripcion por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(inscripcionService.obtenerPorId(id, idInst));
    }

    @GetMapping("/alumno/{idAlumno}")
    public ResponseEntity<List<InscripcionResponseDTO>> listarPorAlumno(@PathVariable Long idAlumno) {
        return ResponseEntity.ok(inscripcionService.listarPorAlumno(idAlumno));
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<InscripcionResponseDTO>> listarPorCurso(@PathVariable Long idCurso) {
        return ResponseEntity.ok(inscripcionService.listarPorCurso(idCurso));
    }

    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> crear(@Valid @RequestBody InscripcionDTO dto) {
        InscripcionResponseDTO creada = inscripcionService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    // DELETE /api/inscripciones/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        inscripcionService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/inscripciones/{id}/reactivar - reactivar inscripcion validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        inscripcionService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
