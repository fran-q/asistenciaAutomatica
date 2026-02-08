package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioProfesorDTO;
import com.appasistencia.models.CategoriaProfesor;
import com.appasistencia.models.UsuarioProfesor;
import com.appasistencia.repositories.UsuarioProfesorRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")
public class UsuarioProfesorController {

    private final UsuarioProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioProfesorController(UsuarioProfesorRepository profesorRepository, UsuarioRepository usuarioRepository) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<UsuarioProfesor> listarTodos() {
        return profesorRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioProfesor> obtenerPorId(@PathVariable Long id) {
        return profesorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioProfesor> crear(@RequestBody UsuarioProfesorDTO dto) {
        return usuarioRepository.findById(dto.getIdUsuario()).map(usuario -> {
            UsuarioProfesor profesor = new UsuarioProfesor(
                    usuario, dto.getLegajo(), dto.getTitulo(),
                    CategoriaProfesor.valueOf(dto.getCategoria())
            );
            return ResponseEntity.ok(profesorRepository.save(profesor));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioProfesor> actualizar(@PathVariable Long id, @RequestBody UsuarioProfesorDTO dto) {
        return profesorRepository.findById(id).map(profesor -> {
            profesor.setLegajo(dto.getLegajo());
            profesor.setTitulo(dto.getTitulo());
            if (dto.getCategoria() != null) profesor.setCategoria(CategoriaProfesor.valueOf(dto.getCategoria()));
            return ResponseEntity.ok(profesorRepository.save(profesor));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return profesorRepository.findById(id).map(profesor -> {
            profesor.setActivo(false);
            profesorRepository.save(profesor);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
