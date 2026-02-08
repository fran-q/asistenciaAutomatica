package com.appasistencia.controllers;

import com.appasistencia.dtos.InscripcionDTO;
import com.appasistencia.models.Inscripcion;
import com.appasistencia.repositories.CursoRepository;
import com.appasistencia.repositories.InscripcionRepository;
import com.appasistencia.repositories.UsuarioAlumnoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioAlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;

    public InscripcionController(InscripcionRepository inscripcionRepository,
                                  UsuarioAlumnoRepository alumnoRepository, CursoRepository cursoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
    }

    @GetMapping
    public List<Inscripcion> listarTodas() {
        return inscripcionRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscripcion> obtenerPorId(@PathVariable Long id) {
        return inscripcionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alumno/{idAlumno}")
    public List<Inscripcion> listarPorAlumno(@PathVariable Long idAlumno) {
        return inscripcionRepository.findByAlumnoIdAlumnoAndActivoTrue(idAlumno);
    }

    @GetMapping("/curso/{idCurso}")
    public List<Inscripcion> listarPorCurso(@PathVariable Long idCurso) {
        return inscripcionRepository.findByCursoIdCursoAndActivoTrue(idCurso);
    }

    @PostMapping
    public ResponseEntity<Inscripcion> crear(@RequestBody InscripcionDTO dto) {
        var alumnoOpt = alumnoRepository.findById(dto.getIdAlumno());
        var cursoOpt = cursoRepository.findById(dto.getIdCurso());

        if (alumnoOpt.isPresent() && cursoOpt.isPresent()) {
            Inscripcion inscripcion = new Inscripcion(alumnoOpt.get(), cursoOpt.get());
            return ResponseEntity.ok(inscripcionRepository.save(inscripcion));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return inscripcionRepository.findById(id).map(inscripcion -> {
            inscripcion.setActivo(false);
            inscripcionRepository.save(inscripcion);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
