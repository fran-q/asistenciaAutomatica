package com.appasistencia.controllers;

import com.appasistencia.dtos.PlantillaBiometricaDTO;
import com.appasistencia.models.PlantillaBiometrica;
import com.appasistencia.repositories.PlantillaBiometricaRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plantillas-biometricas")
public class PlantillaBiometricaController {

    private final PlantillaBiometricaRepository plantillaRepository;
    private final UsuarioRepository usuarioRepository;

    public PlantillaBiometricaController(PlantillaBiometricaRepository plantillaRepository,
                                          UsuarioRepository usuarioRepository) {
        this.plantillaRepository = plantillaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<PlantillaBiometrica> listarTodas() {
        return plantillaRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantillaBiometrica> obtenerPorId(@PathVariable Long id) {
        return plantillaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<PlantillaBiometrica> listarPorUsuario(@PathVariable Long idUsuario) {
        return plantillaRepository.findByUsuarioIdUsuarioAndActivoTrue(idUsuario);
    }

    @PostMapping
    public ResponseEntity<PlantillaBiometrica> crear(@RequestBody PlantillaBiometricaDTO dto) {
        return usuarioRepository.findById(dto.getIdUsuario()).map(usuario -> {
            PlantillaBiometrica plantilla = new PlantillaBiometrica(
                    usuario, null, dto.getCantidadMuestras()
            );
            return ResponseEntity.ok(plantillaRepository.save(plantilla));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return plantillaRepository.findById(id).map(plantilla -> {
            plantilla.setActivo(false);
            plantillaRepository.save(plantilla);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
