package com.appasistencia.controllers;

import com.appasistencia.dtos.AsignacionDTO;
import com.appasistencia.dtos.response.AsignacionResponseDTO;
import com.appasistencia.services.AsignacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de asignaciones profesor-cursoMateria
@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    // GET /api/asignaciones - listar asignaciones por institucion
    @GetMapping
    public ResponseEntity<List<AsignacionResponseDTO>> listarTodas(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(asignacionService.listarTodas(idInst));
    }

    // GET /api/asignaciones/{id} - obtener asignacion por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(asignacionService.obtenerPorId(id, idInst));
    }

    // GET /api/asignaciones/profesor/{idProfesor} - listar asignaciones de un profesor
    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<AsignacionResponseDTO>> listarPorProfesor(@PathVariable Long idProfesor) {
        return ResponseEntity.ok(asignacionService.listarPorProfesor(idProfesor));
    }

    // POST /api/asignaciones - asignar profesor a curso-materia
    @PostMapping
    public ResponseEntity<AsignacionResponseDTO> crear(@Valid @RequestBody AsignacionDTO dto) {
        AsignacionResponseDTO creada = asignacionService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    // DELETE /api/asignaciones/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        asignacionService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/asignaciones/{id}/reactivar - reactivar asignacion validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        asignacionService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
