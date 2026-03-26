package com.appasistencia.controllers;

import com.appasistencia.dtos.AsistenciaDTO;
import com.appasistencia.dtos.response.AsistenciaResponseDTO;
import com.appasistencia.services.AsistenciaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de registros de asistencia
@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping
    public ResponseEntity<List<AsistenciaResponseDTO>> listarTodas(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(asistenciaService.listarTodas(idInst));
    }

    // GET /api/asistencias/{id} - obtener asistencia por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(asistenciaService.obtenerPorId(id, idInst));
    }

    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorProfesor(@PathVariable Long idProfesor) {
        return ResponseEntity.ok(asistenciaService.listarPorProfesor(idProfesor));
    }

    @GetMapping("/asignacion/{idAsignacion}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorAsignacion(@PathVariable Long idAsignacion) {
        return ResponseEntity.ok(asistenciaService.listarPorAsignacion(idAsignacion));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorFecha(@PathVariable String fecha) {
        return ResponseEntity.ok(asistenciaService.listarPorFecha(fecha));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorRango(@RequestParam String desde, @RequestParam String hasta) {
        return ResponseEntity.ok(asistenciaService.listarPorRango(desde, hasta));
    }

    @PostMapping
    public ResponseEntity<AsistenciaResponseDTO> crear(@Valid @RequestBody AsistenciaDTO dto) {
        AsistenciaResponseDTO creada = asistenciaService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    // PUT /api/asistencias/{id} - actualizar asistencia validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AsistenciaDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(asistenciaService.actualizar(id, dto, idInst));
    }

    // DELETE /api/asistencias/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        asistenciaService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/asistencias/{id}/reactivar - reactivar asistencia validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        asistenciaService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
