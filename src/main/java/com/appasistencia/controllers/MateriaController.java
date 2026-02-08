package com.appasistencia.controllers;

import com.appasistencia.dtos.MateriaDTO;
import com.appasistencia.models.Materia;
import com.appasistencia.repositories.CarreraRepository;
import com.appasistencia.repositories.MateriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaRepository materiaRepository;
    private final CarreraRepository carreraRepository;

    public MateriaController(MateriaRepository materiaRepository, CarreraRepository carreraRepository) {
        this.materiaRepository = materiaRepository;
        this.carreraRepository = carreraRepository;
    }

    @GetMapping
    public List<Materia> listarTodas() {
        return materiaRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Materia> obtenerPorId(@PathVariable Long id) {
        return materiaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/carrera/{idCarrera}")
    public List<Materia> listarPorCarrera(@PathVariable Long idCarrera) {
        return materiaRepository.findByCarreraIdCarreraAndActivoTrue(idCarrera);
    }

    @PostMapping
    public ResponseEntity<Materia> crear(@RequestBody MateriaDTO dto) {
        return carreraRepository.findById(dto.getIdCarrera()).map(carrera -> {
            Materia materia = new Materia(dto.getNombre(), dto.getDescripcion(), dto.getHorasSemanales(), carrera);
            return ResponseEntity.ok(materiaRepository.save(materia));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materia> actualizar(@PathVariable Long id, @RequestBody MateriaDTO dto) {
        return materiaRepository.findById(id).map(materia -> {
            materia.setNombre(dto.getNombre());
            materia.setDescripcion(dto.getDescripcion());
            materia.setHorasSemanales(dto.getHorasSemanales());
            if (dto.getIdCarrera() != null) {
                carreraRepository.findById(dto.getIdCarrera()).ifPresent(materia::setCarrera);
            }
            return ResponseEntity.ok(materiaRepository.save(materia));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return materiaRepository.findById(id).map(materia -> {
            materia.setActivo(false);
            materiaRepository.save(materia);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
