package com.appasistencia.controllers;

import com.appasistencia.dtos.HorarioDTO;
import com.appasistencia.dtos.response.HorarioResponseDTO;
import com.appasistencia.services.HorarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de horarios de clase
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    // GET /api/horarios - listar horarios filtrados por institucion
    @GetMapping
    public ResponseEntity<List<HorarioResponseDTO>> listarTodos(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(horarioService.listarTodos(idInst));
    }

    // GET /api/horarios/{id} - obtener horario por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(horarioService.obtenerPorId(id, idInst));
    }

    // GET /api/horarios/asignacion/{idAsignacion} - horarios de una asignacion
    @GetMapping("/asignacion/{idAsignacion}")
    public ResponseEntity<List<HorarioResponseDTO>> listarPorAsignacion(@PathVariable Long idAsignacion) {
        return ResponseEntity.ok(horarioService.listarPorAsignacion(idAsignacion));
    }

    // GET /api/horarios/dia/{dia} - horarios de un dia especifico
    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<HorarioResponseDTO>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(horarioService.listarPorDia(dia));
    }

    // POST /api/horarios - crear nuevo horario
    @PostMapping
    public ResponseEntity<HorarioResponseDTO> crear(@Valid @RequestBody HorarioDTO dto) {
        HorarioResponseDTO creado = horarioService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // PUT /api/horarios/{id} - actualizar horario validando institucion
    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody HorarioDTO dto, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        return ResponseEntity.ok(horarioService.actualizar(id, dto, idInst));
    }

    // DELETE /api/horarios/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        horarioService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/horarios/{id}/reactivar - reactivar horario validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        horarioService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
