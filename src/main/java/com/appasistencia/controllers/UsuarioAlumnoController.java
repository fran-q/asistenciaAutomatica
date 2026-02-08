package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioAlumnoDTO;
import com.appasistencia.models.UsuarioAlumno;
import com.appasistencia.repositories.UsuarioAlumnoRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class UsuarioAlumnoController {

    private final UsuarioAlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioAlumnoController(UsuarioAlumnoRepository alumnoRepository, UsuarioRepository usuarioRepository) {
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<UsuarioAlumno> listarTodos() {
        return alumnoRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioAlumno> obtenerPorId(@PathVariable Long id) {
        return alumnoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioAlumno> crear(@RequestBody UsuarioAlumnoDTO dto) {
        return usuarioRepository.findById(dto.getIdUsuario()).map(usuario -> {
            UsuarioAlumno alumno = new UsuarioAlumno(usuario, dto.getLegajo(), dto.getPromedio());
            return ResponseEntity.ok(alumnoRepository.save(alumno));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioAlumno> actualizar(@PathVariable Long id, @RequestBody UsuarioAlumnoDTO dto) {
        return alumnoRepository.findById(id).map(alumno -> {
            alumno.setLegajo(dto.getLegajo());
            alumno.setPromedio(dto.getPromedio());
            return ResponseEntity.ok(alumnoRepository.save(alumno));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return alumnoRepository.findById(id).map(alumno -> {
            alumno.setActivo(false);
            alumnoRepository.save(alumno);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
