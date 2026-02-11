package com.appasistencia.controllers;

import com.appasistencia.dtos.MateriaDTO;
import com.appasistencia.dtos.response.MateriaResponseDTO;
import com.appasistencia.services.MateriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(materiaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(materiaService.obtenerPorId(id));
    }

    @GetMapping("/carrera/{idCarrera}")
    public ResponseEntity<List<MateriaResponseDTO>> listarPorCarrera(@PathVariable Long idCarrera) {
        return ResponseEntity.ok(materiaService.listarPorCarrera(idCarrera));
    }

    @PostMapping
    public ResponseEntity<MateriaResponseDTO> crear(@Valid @RequestBody MateriaDTO dto) {
        MateriaResponseDTO creada = materiaService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MateriaDTO dto) {
        return ResponseEntity.ok(materiaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        materiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
