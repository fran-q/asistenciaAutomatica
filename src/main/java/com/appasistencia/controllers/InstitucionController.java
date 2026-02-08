package com.appasistencia.controllers;

import com.appasistencia.dtos.InstitucionDTO;
import com.appasistencia.models.Institucion;
import com.appasistencia.repositories.InstitucionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/instituciones")
public class InstitucionController {

    private final InstitucionRepository institucionRepository;

    public InstitucionController(InstitucionRepository institucionRepository) {
        this.institucionRepository = institucionRepository;
    }

    @GetMapping
    public List<Institucion> listarTodas() {
        return institucionRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Institucion> obtenerPorId(@PathVariable Long id) {
        return institucionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Institucion crear(@RequestBody InstitucionDTO dto) {
        Institucion institucion = new Institucion(
                dto.getNombre(), dto.getDireccion(), dto.getTelefono(), dto.getEmail()
        );
        return institucionRepository.save(institucion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Institucion> actualizar(@PathVariable Long id, @RequestBody InstitucionDTO dto) {
        return institucionRepository.findById(id).map(institucion -> {
            institucion.setNombre(dto.getNombre());
            institucion.setDireccion(dto.getDireccion());
            institucion.setTelefono(dto.getTelefono());
            institucion.setEmail(dto.getEmail());
            return ResponseEntity.ok(institucionRepository.save(institucion));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return institucionRepository.findById(id).map(institucion -> {
            institucion.setActivo(false);
            institucionRepository.save(institucion);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
