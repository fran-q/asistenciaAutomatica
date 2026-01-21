package com.appasistencia.controllers;

import com.appasistencia.dtos.PlantillaBiometricaDTO;
import com.appasistencia.models.PlantillaBiometrica;
import com.appasistencia.repositories.PlantillaBiometricaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PlantillaBiometricaController {

    private final PlantillaBiometricaRepository plantillaRepository;

    public PlantillaBiometricaController(PlantillaBiometricaRepository plantillaRepository) {
        this.plantillaRepository = plantillaRepository;
    }

    //Listado de todas las plantillas
    @GetMapping("/plantillas")
    public List<PlantillaBiometricaDTO> getPlantillas() {
        return plantillaRepository.findAll()
                .stream()
                .map(PlantillaBiometricaDTO::new)
                .toList();
    }

    //Plantilla buscada por ID
    @GetMapping("/plantillas/{id}")
    public ResponseEntity<PlantillaBiometricaDTO> getPlantilla(@PathVariable Integer id) {
        return plantillaRepository.findById(id)
                .map(plantilla -> ResponseEntity.ok(new PlantillaBiometricaDTO(plantilla)))
                .orElse(ResponseEntity.notFound().build());
    }

    //Creacion de plantilla, si no se especifica fecha se ingresa la actual
    @PostMapping("/plantillas")
    public ResponseEntity<?> crearPlantilla(@RequestBody PlantillaBiometrica plantilla) {
        if (plantilla.getFechaCreacion() == null) {
            plantilla.setFechaCreacion(LocalDate.now());
        }
        plantilla.setEsActivo(true);
        plantillaRepository.save(plantilla);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Plantilla creada correctamente");
    }

    //Actualizacion de los datos de la plantilla
    @PutMapping("/plantillas/{id}")
    public ResponseEntity<?> actualizarPlantilla(@PathVariable Integer id, @RequestBody PlantillaBiometrica nuevosDatos) {
        return plantillaRepository.findById(id)
                .map(plantilla -> {
                    plantilla.setPlantillaBiometrica(nuevosDatos.getPlantillaBiometrica());
                    plantilla.setFechaCreacion(nuevosDatos.getFechaCreacion());
                    plantillaRepository.save(plantilla);
                    return ResponseEntity.ok("Plantilla actualizada correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //Baja logica de la plantilla
    @PatchMapping("/plantillas/{id}/baja")
    public ResponseEntity<?> darBajaPlantilla(@PathVariable Integer id) {
        return plantillaRepository.findById(id)
                .map(plantilla -> {
                    plantilla.setEsActivo(false);
                    plantillaRepository.save(plantilla);
                    return ResponseEntity.ok("Plantilla dada de baja correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}