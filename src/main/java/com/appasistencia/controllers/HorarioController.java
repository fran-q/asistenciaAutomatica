package com.appasistencia.controllers;

import com.appasistencia.dtos.HorarioDTO;
import com.appasistencia.dtos.response.HorarioResponseDTO;
import com.appasistencia.services.HorarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<List<HorarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(horarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.obtenerPorId(id));
    }

    @GetMapping("/asignacion/{idAsignacion}")
    public ResponseEntity<List<HorarioResponseDTO>> listarPorAsignacion(@PathVariable Long idAsignacion) {
        return ResponseEntity.ok(horarioService.listarPorAsignacion(idAsignacion));
    }

    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<HorarioResponseDTO>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(horarioService.listarPorDia(dia));
    }

    @PostMapping
    public ResponseEntity<HorarioResponseDTO> crear(@Valid @RequestBody HorarioDTO dto) {
        HorarioResponseDTO creado = horarioService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody HorarioDTO dto) {
        return ResponseEntity.ok(horarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
