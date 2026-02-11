package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioAlumnoDTO;
import com.appasistencia.dtos.response.UsuarioAlumnoResponseDTO;
import com.appasistencia.services.UsuarioAlumnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class UsuarioAlumnoController {

    private final UsuarioAlumnoService alumnoService;

    public UsuarioAlumnoController(UsuarioAlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioAlumnoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(alumnoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioAlumnoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alumnoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioAlumnoResponseDTO> crear(@Valid @RequestBody UsuarioAlumnoDTO dto) {
        UsuarioAlumnoResponseDTO creado = alumnoService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioAlumnoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioAlumnoDTO dto) {
        return ResponseEntity.ok(alumnoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alumnoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
