package com.appasistencia.controllers;

import com.appasistencia.dtos.CursoMateriaDTO;
import com.appasistencia.dtos.response.CursoMateriaResponseDTO;
import com.appasistencia.services.CursoMateriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curso-materias")
public class CursoMateriaController {

    private final CursoMateriaService cursoMateriaService;

    public CursoMateriaController(CursoMateriaService cursoMateriaService) {
        this.cursoMateriaService = cursoMateriaService;
    }

    @GetMapping
    public ResponseEntity<List<CursoMateriaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(cursoMateriaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoMateriaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cursoMateriaService.obtenerPorId(id));
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<CursoMateriaResponseDTO>> listarPorCurso(@PathVariable Long idCurso) {
        return ResponseEntity.ok(cursoMateriaService.listarPorCurso(idCurso));
    }

    @PostMapping
    public ResponseEntity<CursoMateriaResponseDTO> crear(@Valid @RequestBody CursoMateriaDTO dto) {
        CursoMateriaResponseDTO creado = cursoMateriaService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoMateriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
