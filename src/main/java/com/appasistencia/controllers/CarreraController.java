package com.appasistencia.controllers;

import com.appasistencia.dtos.CarreraDTO;
import com.appasistencia.dtos.response.CarreraResponseDTO;
import com.appasistencia.services.CarreraService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de carreras
@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    // GET /api/carreras - listar carreras filtradas por institucion del JWT
    @GetMapping
    public ResponseEntity<List<CarreraResponseDTO>> listarTodas(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(carreraService.listarTodas(idInst));
    }

    // GET /api/carreras/{id} - obtener carrera por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(carreraService.obtenerPorId(id, idInst));
    }

    // POST /api/carreras - crear carrera (asigna institucion del JWT)
    @PostMapping
    public ResponseEntity<CarreraResponseDTO> crear(@Valid @RequestBody CarreraDTO dto, HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        dto.setIdInstitucion(idInst);
        CarreraResponseDTO creada = carreraService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    // PUT /api/carreras/{id} - actualizar carrera validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CarreraDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        dto.setIdInstitucion(idInst);
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(carreraService.actualizar(id, dto, idInst));
    }

    // DELETE /api/carreras/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        carreraService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/carreras/{id}/reactivar - reactivar carrera validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        carreraService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
