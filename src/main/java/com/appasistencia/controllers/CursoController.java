package com.appasistencia.controllers;

import com.appasistencia.dtos.CursoDTO;
import com.appasistencia.models.Curso;
import com.appasistencia.models.Turno;
import com.appasistencia.repositories.CarreraRepository;
import com.appasistencia.repositories.CursoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;

    public CursoController(CursoRepository cursoRepository, CarreraRepository carreraRepository) {
        this.cursoRepository = cursoRepository;
        this.carreraRepository = carreraRepository;
    }

    @GetMapping
    public List<Curso> listarTodos() {
        return cursoRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtenerPorId(@PathVariable Long id) {
        return cursoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/carrera/{idCarrera}")
    public List<Curso> listarPorCarrera(@PathVariable Long idCarrera) {
        return cursoRepository.findByCarreraIdCarreraAndActivoTrue(idCarrera);
    }

    @PostMapping
    public ResponseEntity<Curso> crear(@RequestBody CursoDTO dto) {
        return carreraRepository.findById(dto.getIdCarrera()).map(carrera -> {
            Curso curso = new Curso(
                    dto.getNombre(), dto.getAnioCarrera(), dto.getComision(),
                    Turno.valueOf(dto.getTurno()), carrera, dto.getAnioLectivo()
            );
            return ResponseEntity.ok(cursoRepository.save(curso));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizar(@PathVariable Long id, @RequestBody CursoDTO dto) {
        return cursoRepository.findById(id).map(curso -> {
            curso.setNombre(dto.getNombre());
            curso.setAnioCarrera(dto.getAnioCarrera());
            curso.setComision(dto.getComision());
            if (dto.getTurno() != null) curso.setTurno(Turno.valueOf(dto.getTurno()));
            curso.setAnioLectivo(dto.getAnioLectivo());
            if (dto.getIdCarrera() != null) {
                carreraRepository.findById(dto.getIdCarrera()).ifPresent(curso::setCarrera);
            }
            return ResponseEntity.ok(cursoRepository.save(curso));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return cursoRepository.findById(id).map(curso -> {
            curso.setActivo(false);
            cursoRepository.save(curso);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
