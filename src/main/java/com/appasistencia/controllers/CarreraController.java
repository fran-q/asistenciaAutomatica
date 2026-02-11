package com.appasistencia.controllers;

import com.appasistencia.dtos.CarreraDTO;
import com.appasistencia.dtos.response.CarreraResponseDTO;
import com.appasistencia.services.CarreraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping
    public ResponseEntity<List<CarreraResponseDTO>> listarTodas() {
        return ResponseEntity.ok(carreraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.obtenerPorId(id));
    }

    @GetMapping("/institucion/{idInstitucion}")
    public ResponseEntity<List<CarreraResponseDTO>> listarPorInstitucion(@PathVariable Long idInstitucion) {
        return ResponseEntity.ok(carreraService.listarPorInstitucion(idInstitucion));
    }

    @PostMapping
    public ResponseEntity<CarreraResponseDTO> crear(@Valid @RequestBody CarreraDTO dto) {
        CarreraResponseDTO creada = carreraService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CarreraDTO dto) {
        return ResponseEntity.ok(carreraService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
