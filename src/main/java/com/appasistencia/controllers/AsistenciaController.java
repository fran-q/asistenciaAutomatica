package com.appasistencia.controllers;

import com.appasistencia.dtos.AsistenciaDTO;
import com.appasistencia.dtos.response.AsistenciaResponseDTO;
import com.appasistencia.services.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping
    public ResponseEntity<List<AsistenciaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(asistenciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asistenciaService.obtenerPorId(id));
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

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AsistenciaDTO dto) {
        return ResponseEntity.ok(asistenciaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
