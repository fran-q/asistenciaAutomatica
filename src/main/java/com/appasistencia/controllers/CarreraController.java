package com.appasistencia.controllers;

import com.appasistencia.dtos.CarreraDTO;
import com.appasistencia.models.Carrera;
import com.appasistencia.repositories.CarreraRepository;
import com.appasistencia.repositories.InstitucionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraRepository carreraRepository;
    private final InstitucionRepository institucionRepository;

    public CarreraController(CarreraRepository carreraRepository, InstitucionRepository institucionRepository) {
        this.carreraRepository = carreraRepository;
        this.institucionRepository = institucionRepository;
    }

    @GetMapping
    public List<Carrera> listarTodas() {
        return carreraRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrera> obtenerPorId(@PathVariable Long id) {
        return carreraRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/institucion/{idInstitucion}")
    public List<Carrera> listarPorInstitucion(@PathVariable Long idInstitucion) {
        return carreraRepository.findByInstitucionIdInstitucionAndActivoTrue(idInstitucion);
    }

    @PostMapping
    public ResponseEntity<Carrera> crear(@RequestBody CarreraDTO dto) {
        return institucionRepository.findById(dto.getIdInstitucion()).map(institucion -> {
            Carrera carrera = new Carrera(
                    dto.getNombre(), dto.getDescripcion(), dto.getDuracionAnios(),
                    dto.getTitulo(), institucion
            );
            return ResponseEntity.ok(carreraRepository.save(carrera));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrera> actualizar(@PathVariable Long id, @RequestBody CarreraDTO dto) {
        return carreraRepository.findById(id).map(carrera -> {
            carrera.setNombre(dto.getNombre());
            carrera.setDescripcion(dto.getDescripcion());
            carrera.setDuracionAnios(dto.getDuracionAnios());
            carrera.setTitulo(dto.getTitulo());
            if (dto.getIdInstitucion() != null) {
                institucionRepository.findById(dto.getIdInstitucion())
                        .ifPresent(carrera::setInstitucion);
            }
            return ResponseEntity.ok(carreraRepository.save(carrera));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return carreraRepository.findById(id).map(carrera -> {
            carrera.setActivo(false);
            carreraRepository.save(carrera);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
