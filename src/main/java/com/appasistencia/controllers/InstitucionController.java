package com.appasistencia.controllers;

import com.appasistencia.dtos.InstitucionDTO;
import com.appasistencia.dtos.response.InstitucionResponseDTO;
import com.appasistencia.services.InstitucionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instituciones")
public class InstitucionController {

    private final InstitucionService institucionService;

    public InstitucionController(InstitucionService institucionService) {
        this.institucionService = institucionService;
    }

    @GetMapping
    public ResponseEntity<List<InstitucionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(institucionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstitucionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(institucionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<InstitucionResponseDTO> crear(@Valid @RequestBody InstitucionDTO dto) {
        InstitucionResponseDTO creada = institucionService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitucionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody InstitucionDTO dto) {
        return ResponseEntity.ok(institucionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        institucionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
