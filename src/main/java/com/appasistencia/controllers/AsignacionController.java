package com.appasistencia.controllers;

import com.appasistencia.dtos.AsignacionDTO;
import com.appasistencia.dtos.response.AsignacionResponseDTO;
import com.appasistencia.services.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @GetMapping
    public ResponseEntity<List<AsignacionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(asignacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.obtenerPorId(id));
    }

    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<AsignacionResponseDTO>> listarPorProfesor(@PathVariable Long idProfesor) {
        return ResponseEntity.ok(asignacionService.listarPorProfesor(idProfesor));
    }

    @PostMapping
    public ResponseEntity<AsignacionResponseDTO> crear(@Valid @RequestBody AsignacionDTO dto) {
        AsignacionResponseDTO creada = asignacionService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
