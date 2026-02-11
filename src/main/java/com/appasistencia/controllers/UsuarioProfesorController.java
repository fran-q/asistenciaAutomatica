package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioProfesorDTO;
import com.appasistencia.dtos.response.UsuarioProfesorResponseDTO;
import com.appasistencia.services.UsuarioProfesorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")
public class UsuarioProfesorController {

    private final UsuarioProfesorService profesorService;

    public UsuarioProfesorController(UsuarioProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioProfesorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(profesorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioProfesorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profesorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioProfesorResponseDTO> crear(@Valid @RequestBody UsuarioProfesorDTO dto) {
        UsuarioProfesorResponseDTO creado = profesorService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioProfesorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioProfesorDTO dto) {
        return ResponseEntity.ok(profesorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        profesorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
