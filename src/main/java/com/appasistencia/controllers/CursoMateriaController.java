package com.appasistencia.controllers;

import com.appasistencia.dtos.CursoMateriaDTO;
import com.appasistencia.models.CursoMateria;
import com.appasistencia.repositories.CursoMateriaRepository;
import com.appasistencia.repositories.CursoRepository;
import com.appasistencia.repositories.MateriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curso-materias")
public class CursoMateriaController {

    private final CursoMateriaRepository cursoMateriaRepository;
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;

    public CursoMateriaController(CursoMateriaRepository cursoMateriaRepository,
                                   CursoRepository cursoRepository, MateriaRepository materiaRepository) {
        this.cursoMateriaRepository = cursoMateriaRepository;
        this.cursoRepository = cursoRepository;
        this.materiaRepository = materiaRepository;
    }

    @GetMapping
    public List<CursoMateria> listarTodos() {
        return cursoMateriaRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoMateria> obtenerPorId(@PathVariable Long id) {
        return cursoMateriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/curso/{idCurso}")
    public List<CursoMateria> listarPorCurso(@PathVariable Long idCurso) {
        return cursoMateriaRepository.findByCursoIdCursoAndActivoTrue(idCurso);
    }

    @PostMapping
    public ResponseEntity<CursoMateria> crear(@RequestBody CursoMateriaDTO dto) {
        var cursoOpt = cursoRepository.findById(dto.getIdCurso());
        var materiaOpt = materiaRepository.findById(dto.getIdMateria());

        if (cursoOpt.isPresent() && materiaOpt.isPresent()) {
            CursoMateria cm = new CursoMateria(cursoOpt.get(), materiaOpt.get());
            return ResponseEntity.ok(cursoMateriaRepository.save(cm));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return cursoMateriaRepository.findById(id).map(cm -> {
            cm.setActivo(false);
            cursoMateriaRepository.save(cm);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
