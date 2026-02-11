package com.appasistencia.controllers;

import com.appasistencia.dtos.InscripcionDTO;
import com.appasistencia.dtos.response.InscripcionResponseDTO;
import com.appasistencia.services.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(inscripcionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.obtenerPorId(id));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inscripcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
