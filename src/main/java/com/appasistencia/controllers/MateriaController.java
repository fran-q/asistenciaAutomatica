package com.appasistencia.controllers;

import com.appasistencia.dtos.MateriaDTO;
import com.appasistencia.dtos.response.MateriaResponseDTO;
import com.appasistencia.services.MateriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de materias
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    // GET /api/materias - listar materias filtradas por institucion del JWT
    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> listarTodas(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(materiaService.listarTodas(idInst));
    }

    // GET /api/materias/{id} - obtener materia por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(materiaService.obtenerPorId(id, idInst));
    }

    // GET /api/materias/carrera/{idCarrera} - listar materias de una carrera
    @GetMapping("/carrera/{idCarrera}")
    public ResponseEntity<List<MateriaResponseDTO>> listarPorCarrera(@PathVariable Long idCarrera) {
        return ResponseEntity.ok(materiaService.listarPorCarrera(idCarrera));
    }

    // POST /api/materias - crear nueva materia
    @PostMapping
    public ResponseEntity<MateriaResponseDTO> crear(@Valid @RequestBody MateriaDTO dto) {
        MateriaResponseDTO creada = materiaService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    // PUT /api/materias/{id} - actualizar materia validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MateriaDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(materiaService.actualizar(id, dto, idInst));
    }

    // DELETE /api/materias/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        materiaService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/materias/{id}/reactivar - reactivar materia validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        materiaService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
