package com.appasistencia.controllers;

import com.appasistencia.dtos.InstitucionDTO;
import com.appasistencia.dtos.response.InstitucionResponseDTO;
import com.appasistencia.services.InstitucionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de instituciones
@RestController
@RequestMapping("/api/instituciones")
public class InstitucionController {

    private final InstitucionService institucionService;

    public InstitucionController(InstitucionService institucionService) {
        this.institucionService = institucionService;
    }

    // GET /api/instituciones - retorna solo la institucion del usuario autenticado
    @GetMapping
    public ResponseEntity<List<InstitucionResponseDTO>> listarTodas(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(List.of(institucionService.obtenerPorId(idInst)));
    }

    // GET /api/instituciones/{id} - obtener institucion por ID
    @GetMapping("/{id}")
    public ResponseEntity<InstitucionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(institucionService.obtenerPorId(id));
    }

    // POST /api/instituciones - crear nueva institucion
    @PostMapping
    public ResponseEntity<InstitucionResponseDTO> crear(@Valid @RequestBody InstitucionDTO dto) {
        InstitucionResponseDTO creada = institucionService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    // PUT /api/instituciones/{id} - actualizar institucion
    @PutMapping("/{id}")
    public ResponseEntity<InstitucionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody InstitucionDTO dto) {
        return ResponseEntity.ok(institucionService.actualizar(id, dto));
    }

    // DELETE /api/instituciones/{id} - baja logica
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        institucionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/instituciones/{id}/reactivar - reactivar institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) {
        institucionService.reactivar(id);
        return ResponseEntity.noContent().build();
    }
}
