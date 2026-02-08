package com.appasistencia.controllers;

import com.appasistencia.dtos.AsignacionDTO;
import com.appasistencia.models.Asignacion;
import com.appasistencia.repositories.AsignacionRepository;
import com.appasistencia.repositories.CursoMateriaRepository;
import com.appasistencia.repositories.UsuarioProfesorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionRepository asignacionRepository;
    private final UsuarioProfesorRepository profesorRepository;
    private final CursoMateriaRepository cursoMateriaRepository;

    public AsignacionController(AsignacionRepository asignacionRepository,
                                 UsuarioProfesorRepository profesorRepository,
                                 CursoMateriaRepository cursoMateriaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.profesorRepository = profesorRepository;
        this.cursoMateriaRepository = cursoMateriaRepository;
    }

    @GetMapping
    public List<Asignacion> listarTodas() {
        return asignacionRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignacion> obtenerPorId(@PathVariable Long id) {
        return asignacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profesor/{idProfesor}")
    public List<Asignacion> listarPorProfesor(@PathVariable Long idProfesor) {
        return asignacionRepository.findByProfesorIdProfesorAndActivoTrue(idProfesor);
    }

    @PostMapping
    public ResponseEntity<Asignacion> crear(@RequestBody AsignacionDTO dto) {
        var profesorOpt = profesorRepository.findById(dto.getIdProfesor());
        var cmOpt = cursoMateriaRepository.findById(dto.getIdCursoMateria());

        if (profesorOpt.isPresent() && cmOpt.isPresent()) {
            Asignacion asignacion = new Asignacion(profesorOpt.get(), cmOpt.get());
            return ResponseEntity.ok(asignacionRepository.save(asignacion));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return asignacionRepository.findById(id).map(asignacion -> {
            asignacion.setActivo(false);
            asignacionRepository.save(asignacion);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
