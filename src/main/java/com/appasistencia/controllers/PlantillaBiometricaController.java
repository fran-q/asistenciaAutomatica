package com.appasistencia.controllers;

import com.appasistencia.dtos.PlantillaBiometricaDTO;
import com.appasistencia.dtos.response.PlantillaBiometricaResponseDTO;
import com.appasistencia.services.PlantillaBiometricaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de plantillas biometricas faciales
@RestController
@RequestMapping("/api/plantillas-biometricas")
public class PlantillaBiometricaController {

    private final PlantillaBiometricaService plantillaService;

    public PlantillaBiometricaController(PlantillaBiometricaService plantillaService) {
        this.plantillaService = plantillaService;
    }

    @GetMapping
    public ResponseEntity<List<PlantillaBiometricaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(plantillaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantillaBiometricaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(plantillaService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PlantillaBiometricaResponseDTO>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(plantillaService.listarPorUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<PlantillaBiometricaResponseDTO> crear(@Valid @RequestBody PlantillaBiometricaDTO dto) {
        PlantillaBiometricaResponseDTO creada = plantillaService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        plantillaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
